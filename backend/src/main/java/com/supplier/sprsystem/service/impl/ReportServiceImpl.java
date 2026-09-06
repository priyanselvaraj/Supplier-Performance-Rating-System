package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.DashboardAnalyticsService;
import com.supplier.sprsystem.service.ReportService;
import com.supplier.sprsystem.service.SupplierRatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final SupplierRepository supplierRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierPerformanceRatingRepository performanceRatingRepository;
    private final SupplierRatingService supplierRatingService;
    private final DashboardAnalyticsService dashboardAnalyticsService;

    public ReportServiceImpl(SupplierRepository supplierRepository,
                             SupplierEvaluationRepository evaluationRepository,
                             SupplierPerformanceRatingRepository performanceRatingRepository,
                             SupplierRatingService supplierRatingService,
                             DashboardAnalyticsService dashboardAnalyticsService) {
        this.supplierRepository = supplierRepository;
        this.evaluationRepository = evaluationRepository;
        this.performanceRatingRepository = performanceRatingRepository;
        this.supplierRatingService = supplierRatingService;
        this.dashboardAnalyticsService = dashboardAnalyticsService;
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceReportResponse getSupplierPerformanceReport(Long supplierId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        SupplierPerformanceSummaryResponse summary = supplierRatingService.getSupplierPerformanceSummary(supplierId);

        List<SupplierPerformanceRating> allRatings = performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId);
        if (startDate != null) {
            allRatings = allRatings.stream().filter(r -> !r.getRatingDate().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            allRatings = allRatings.stream().filter(r -> !r.getRatingDate().isAfter(endDate)).collect(Collectors.toList());
        }

        List<SupplierPerformanceRatingResponse> history = allRatings.stream()
                .map(this::mapToRatingResponse)
                .collect(Collectors.toList());

        return SupplierPerformanceReportResponse.builder()
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .categoryName(supplier.getCategory() != null ? supplier.getCategory().getName() : "N/A")
                .active(supplier.getStatus() == SupplierStatus.ACTIVE)
                .latestScore(summary.getLatestScore())
                .latestRating(summary.getLatestRating())
                .latestRatingDisplayName(summary.getLatestRatingDisplayName())
                .performanceStatus(summary.getLatestPerformanceStatus())
                .performanceStatusDisplayName(summary.getLatestPerformanceStatusDisplayName())
                .previousScore(summary.getPreviousScore())
                .scoreDifference(summary.getScoreDifference())
                .performanceTrend(summary.getPerformanceTrend())
                .performanceTrendDisplayName(summary.getPerformanceTrendDisplayName())
                .latestRatingDate(summary.getRatingDate())
                .ratingHistory(history)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierEvaluationReportResponse getSupplierEvaluationReport(Long evaluationId) {
        SupplierEvaluation eval = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", evaluationId));

        List<CriteriaScoreReportResponse> criteriaScores = eval.getScores().stream()
                .map(s -> CriteriaScoreReportResponse.builder()
                        .criteriaId(s.getCriteria().getId())
                        .criteriaName(s.getCriteria().getName())
                        .criteriaCode(s.getCriteria().getCode())
                        .weight(s.getWeight())
                        .rawScore(s.getScoreObtained())
                        .maxScore(s.getMaxScore())
                        .weightedScore(s.getWeightedScore())
                        .comments(s.getRemarks())
                        .build())
                .collect(Collectors.toList());

        return SupplierEvaluationReportResponse.builder()
                .evaluationId(eval.getId())
                .evaluationCode(eval.getEvaluationCode())
                .supplierId(eval.getSupplier().getId())
                .supplierCode(eval.getSupplier().getSupplierCode())
                .supplierName(eval.getSupplier().getName())
                .categoryName(eval.getSupplier().getCategory() != null ? eval.getSupplier().getCategory().getName() : "N/A")
                .evaluatorName(eval.getEvaluator() != null ? eval.getEvaluator().getFullName() : "N/A")
                .evaluationDate(eval.getEvaluationDate())
                .evaluationPeriod(eval.getEvaluationPeriod())
                .status(eval.getStatus())
                .totalScore(eval.getTotalWeightedScore())
                .generalComments(eval.getGeneralComments())
                .strengths(eval.getStrengths())
                .areasForImprovement(eval.getAreasForImprovement())
                .recommendation(eval.getRecommendation())
                .criteriaScores(criteriaScores)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierPerformanceRatingResponse> getSupplierRatingHistoryReport(Long supplierId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", "id", supplierId);
        }

        List<SupplierPerformanceRating> ratings = performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId);
        if (startDate != null) {
            ratings = ratings.stream().filter(r -> !r.getRatingDate().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            ratings = ratings.stream().filter(r -> !r.getRatingDate().isAfter(endDate)).collect(Collectors.toList());
        }

        return ratings.stream().map(this::mapToRatingResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OverallPerformanceReportResponse getOverallPerformanceReport(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByStatus(SupplierStatus.ACTIVE);
        long inactiveSuppliers = supplierRepository.countByStatus(SupplierStatus.INACTIVE);

        SupplierPerformanceAnalyticsResponse analytics = dashboardAnalyticsService.getPerformanceAnalytics(startDate, endDate);
        List<RatingDistributionResponse> ratingDist = dashboardAnalyticsService.getRatingDistribution();
        List<PerformanceStatusDistributionResponse> statusDist = dashboardAnalyticsService.getPerformanceStatusDistribution();
        List<TopSupplierResponse> topSuppliers = dashboardAnalyticsService.getTopPerformingSuppliers(5);
        List<TopSupplierResponse> lowSuppliers = dashboardAnalyticsService.getLowPerformingSuppliers(5);

        return OverallPerformanceReportResponse.builder()
                .generatedAt(LocalDate.now())
                .totalSuppliers(totalSuppliers)
                .activeSuppliers(activeSuppliers)
                .inactiveSuppliers(inactiveSuppliers)
                .totalRatedSuppliers(analytics.getTotalRatedSuppliers())
                .averagePerformanceScore(analytics.getAverageScore())
                .highestPerformanceScore(analytics.getHighestScore())
                .lowestPerformanceScore(analytics.getLowestScore())
                .ratingDistribution(ratingDist)
                .performanceStatusDistribution(statusDist)
                .topSuppliers(topSuppliers)
                .lowPerformingSuppliers(lowSuppliers)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationSummaryReportResponse getEvaluationSummaryReport(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<SupplierEvaluation> evals = evaluationRepository.findAll();
        if (startDate != null) {
            evals = evals.stream().filter(e -> !e.getEvaluationDate().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            evals = evals.stream().filter(e -> !e.getEvaluationDate().isAfter(endDate)).collect(Collectors.toList());
        }

        long total = evals.size();
        long draft = evals.stream().filter(e -> e.getStatus() == EvaluationStatus.DRAFT).count();
        long submitted = evals.stream().filter(e -> e.getStatus() == EvaluationStatus.SUBMITTED).count();
        long completed = evals.stream().filter(e -> e.getStatus() == EvaluationStatus.COMPLETED).count();
        long cancelled = evals.stream().filter(e -> e.getStatus() == EvaluationStatus.CANCELLED).count();

        double avgScore = evals.stream()
                .filter(e -> e.getStatus() == EvaluationStatus.COMPLETED || e.getStatus() == EvaluationStatus.SUBMITTED)
                .mapToDouble(e -> e.getTotalWeightedScore() != null ? e.getTotalWeightedScore() : 0.0)
                .average()
                .orElse(0.0);

        avgScore = BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return EvaluationSummaryReportResponse.builder()
                .generatedAt(LocalDate.now())
                .totalEvaluations(total)
                .draftEvaluations(draft)
                .submittedEvaluations(submitted)
                .completedEvaluations(completed)
                .cancelledEvaluations(cancelled)
                .averageEvaluationScore(avgScore)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReportResponse generatePerformanceReport(
            String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, LocalDate startDate, LocalDate endDate) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Supplier> filteredSuppliers = supplierRepository.filterSuppliersList(
                cleanKeyword, categoryId, status, ratingCategory);

        List<SupplierResponse> supplierResponses = filteredSuppliers.stream()
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());

        List<SupplierEvaluation> filteredEvaluations = evaluationRepository.filterEvaluationsList(
                null, ratingCategory, startDate, endDate);

        List<EvaluationResponse> evaluationResponses = filteredEvaluations.stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());

        Map<String, Long> categoryCount = new HashMap<>();
        for (Supplier s : filteredSuppliers) {
            if (s.getCategory() != null) {
                categoryCount.put(s.getCategory().getName(),
                        categoryCount.getOrDefault(s.getCategory().getName(), 0L) + 1);
            }
        }

        Map<String, Long> ratingCount = new HashMap<>();
        for (Supplier s : filteredSuppliers) {
            ratingCount.put(s.getRatingCategory().name(),
                    ratingCount.getOrDefault(s.getRatingCategory().name(), 0L) + 1);
        }

        double avgSystemScore = filteredSuppliers.stream()
                .filter(s -> s.getTotalEvaluations() > 0)
                .mapToDouble(Supplier::getOverallRating)
                .average()
                .orElse(0.0);

        avgSystemScore = BigDecimal.valueOf(avgSystemScore).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return PerformanceReportResponse.builder()
                .generatedAt(LocalDate.now())
                .totalSuppliersEvaluated(filteredSuppliers.stream().filter(s -> s.getTotalEvaluations() > 0).count())
                .overallSystemAverageScore(avgSystemScore)
                .categoryCountBreakdown(categoryCount)
                .ratingCategoryBreakdown(ratingCount)
                .suppliers(supplierResponses)
                .evaluations(evaluationResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getTopPerformingSuppliers(int limit) {
        return supplierRepository.findTop5ByOrderByOverallRatingDesc().stream()
                .filter(s -> s.getTotalEvaluations() > 0)
                .limit(limit > 0 ? limit : 5)
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getLowPerformingSuppliers(int limit) {
        return supplierRepository.findTop5ByTotalEvaluationsGreaterThanOrderByOverallRatingAsc(0).stream()
                .limit(limit > 0 ? limit : 5)
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getSupplierEvaluationHistoryReport(Long supplierId) {
        return evaluationRepository.findBySupplierIdOrderByEvaluationDateDesc(supplierId).stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }

    private SupplierPerformanceRatingResponse mapToRatingResponse(SupplierPerformanceRating r) {
        return SupplierPerformanceRatingResponse.builder()
                .id(r.getId())
                .supplierId(r.getSupplier().getId())
                .supplierCode(r.getSupplier().getSupplierCode())
                .supplierName(r.getSupplier().getName())
                .evaluationId(r.getEvaluation() != null ? r.getEvaluation().getId() : null)
                .evaluationCode(r.getEvaluation() != null ? r.getEvaluation().getEvaluationCode() : null)
                .score(r.getScore())
                .rating(r.getRating())
                .ratingDisplayName(r.getRating().getDisplayName())
                .performanceStatus(r.getPerformanceStatus())
                .performanceStatusDisplayName(r.getPerformanceStatus().getDisplayName())
                .ratingDate(r.getRatingDate())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        SupplierCategoryResponse catResponse = null;
        if (supplier.getCategory() != null) {
            catResponse = SupplierCategoryResponse.builder()
                    .id(supplier.getCategory().getId())
                    .name(supplier.getCategory().getName())
                    .code(supplier.getCategory().getCode())
                    .description(supplier.getCategory().getDescription())
                    .createdAt(supplier.getCategory().getCreatedAt())
                    .updatedAt(supplier.getCategory().getUpdatedAt())
                    .build();
        }

        return SupplierResponse.builder()
                .id(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .country(supplier.getCountry())
                .category(catResponse)
                .status(supplier.getStatus())
                .overallRating(supplier.getOverallRating())
                .ratingCategory(supplier.getRatingCategory())
                .totalEvaluations(supplier.getTotalEvaluations())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    private EvaluationResponse mapToEvaluationResponse(SupplierEvaluation eval) {
        List<EvaluationScoreResponse> scoreResponses = eval.getScores().stream()
                .map(s -> EvaluationScoreResponse.builder()
                        .id(s.getId())
                        .criteriaId(s.getCriteria().getId())
                        .criteriaName(s.getCriteria().getName())
                        .criteriaCode(s.getCriteria().getCode())
                        .scoreObtained(s.getScoreObtained())
                        .maxScore(s.getMaxScore())
                        .weight(s.getWeight())
                        .weightedScore(s.getWeightedScore())
                        .remarks(s.getRemarks())
                        .build())
                .collect(Collectors.toList());

        return EvaluationResponse.builder()
                .id(eval.getId())
                .evaluationCode(eval.getEvaluationCode())
                .supplierId(eval.getSupplier().getId())
                .supplierCode(eval.getSupplier().getSupplierCode())
                .supplierName(eval.getSupplier().getName())
                .supplierCategoryName(eval.getSupplier().getCategory() != null ? eval.getSupplier().getCategory().getName() : "N/A")
                .evaluatorId(eval.getEvaluator().getId())
                .evaluatorName(eval.getEvaluator().getFullName())
                .evaluatorUsername(eval.getEvaluator().getUsername())
                .evaluationDate(eval.getEvaluationDate())
                .evaluationPeriod(eval.getEvaluationPeriod())
                .totalWeightedScore(eval.getTotalWeightedScore())
                .ratingCategory(eval.getRatingCategory())
                .generalComments(eval.getGeneralComments())
                .strengths(eval.getStrengths())
                .areasForImprovement(eval.getAreasForImprovement())
                .recommendation(eval.getRecommendation())
                .scores(scoreResponses)
                .createdAt(eval.getCreatedAt())
                .build();
    }
}
