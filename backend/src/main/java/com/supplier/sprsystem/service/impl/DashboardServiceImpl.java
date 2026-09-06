package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.*;
import com.supplier.sprsystem.service.DashboardAnalyticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardAnalyticsService {

    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository categoryRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierPerformanceRatingRepository performanceRatingRepository;

    public DashboardServiceImpl(SupplierRepository supplierRepository,
                                SupplierCategoryRepository categoryRepository,
                                SupplierEvaluationRepository evaluationRepository,
                                SupplierPerformanceRatingRepository performanceRatingRepository) {
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.evaluationRepository = evaluationRepository;
        this.performanceRatingRepository = performanceRatingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByActive(true);
        long inactiveSuppliers = supplierRepository.countByActive(false);
        long pendingSuppliers = supplierRepository.countByStatus(SupplierStatus.PENDING_REVIEW);

        long totalEvaluations = evaluationRepository.count();
        long draftEvaluations = evaluationRepository.countByStatus(EvaluationStatus.DRAFT);
        long submittedEvaluations = evaluationRepository.countByStatus(EvaluationStatus.SUBMITTED);
        long completedEvaluations = evaluationRepository.countByStatus(EvaluationStatus.COMPLETED);
        long cancelledEvaluations = evaluationRepository.countByStatus(EvaluationStatus.CANCELLED);

        List<SupplierPerformanceRating> latestRatings = getLatestRatingsForDistinctSuppliers();
        long totalRated = latestRatings.size();

        double avgScore = 0.0;
        long highPerf = 0;
        long satisfactory = 0;
        long needsImp = 0;
        long lowPerf = 0;

        if (totalRated > 0) {
            double sum = 0.0;
            for (SupplierPerformanceRating r : latestRatings) {
                sum += r.getScore();
                if (r.getPerformanceStatus() == PerformanceStatus.HIGH_PERFORMING) highPerf++;
                else if (r.getPerformanceStatus() == PerformanceStatus.SATISFACTORY) satisfactory++;
                else if (r.getPerformanceStatus() == PerformanceStatus.NEEDS_IMPROVEMENT) needsImp++;
                else if (r.getPerformanceStatus() == PerformanceStatus.LOW_PERFORMING) lowPerf++;
            }
            avgScore = round(sum / totalRated, 2);
        }

        long excellentCount = supplierRepository.countByRatingCategory(RatingCategory.EXCELLENT);
        long goodCount = supplierRepository.countByRatingCategory(RatingCategory.GOOD);
        long averageCount = supplierRepository.countByRatingCategory(RatingCategory.AVERAGE);
        long poorCount = supplierRepository.countByRatingCategory(RatingCategory.POOR);
        long unratedCount = supplierRepository.countByRatingCategory(RatingCategory.UNRATED);

        Map<String, Long> ratingDistribution = new HashMap<>();
        ratingDistribution.put("EXCELLENT", excellentCount);
        ratingDistribution.put("GOOD", goodCount);
        ratingDistribution.put("AVERAGE", averageCount);
        ratingDistribution.put("POOR", poorCount);
        ratingDistribution.put("UNRATED", unratedCount);

        List<SupplierResponse> topSuppliers = supplierRepository.findTop5ByOrderByOverallRatingDesc().stream()
                .filter(s -> s.getTotalEvaluations() > 0)
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());

        List<SupplierResponse> lowSuppliers = supplierRepository.findTop5ByTotalEvaluationsGreaterThanOrderByOverallRatingAsc(0).stream()
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());

        List<EvaluationResponse> recentEvaluations = evaluationRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .totalSuppliers(totalSuppliers)
                .activeSuppliers(activeSuppliers)
                .inactiveSuppliers(inactiveSuppliers)
                .pendingSuppliers(pendingSuppliers)
                .totalEvaluations(totalEvaluations)
                .draftEvaluations(draftEvaluations)
                .submittedEvaluations(submittedEvaluations)
                .completedEvaluations(completedEvaluations)
                .cancelledEvaluations(cancelledEvaluations)
                .averagePerformanceScore(avgScore)
                .highPerformingSuppliers(highPerf)
                .satisfactorySuppliers(satisfactory)
                .needsImprovementSuppliers(needsImp)
                .lowPerformingSuppliers(lowPerf)
                .excellentSuppliersCount(excellentCount)
                .goodSuppliersCount(goodCount)
                .averageSuppliersCount(averageCount)
                .poorSuppliersCount(poorCount)
                .unratedSuppliersCount(unratedCount)
                .ratingDistribution(ratingDistribution)
                .topPerformingSuppliers(topSuppliers)
                .lowPerformingSuppliers(lowSuppliers)
                .recentEvaluations(recentEvaluations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierStatisticsResponse getSupplierStatistics() {
        long total = supplierRepository.count();
        long active = supplierRepository.countByActive(true);
        long inactive = supplierRepository.countByActive(false);
        List<CategorySupplierStatisticsResponse> catStats = getSuppliersByCategory();

        return SupplierStatisticsResponse.builder()
                .totalSuppliers(total)
                .activeSuppliers(active)
                .inactiveSuppliers(inactive)
                .suppliersByCategory(catStats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationStatisticsResponse getEvaluationStatistics() {
        long total = evaluationRepository.count();
        long draft = evaluationRepository.countByStatus(EvaluationStatus.DRAFT);
        long submitted = evaluationRepository.countByStatus(EvaluationStatus.SUBMITTED);
        long completed = evaluationRepository.countByStatus(EvaluationStatus.COMPLETED);
        long cancelled = evaluationRepository.countByStatus(EvaluationStatus.CANCELLED);

        return EvaluationStatisticsResponse.builder()
                .total(total)
                .draft(draft)
                .submitted(submitted)
                .completed(completed)
                .cancelled(cancelled)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceAnalyticsResponse getPerformanceAnalytics(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        List<SupplierPerformanceRating> ratings = performanceRatingRepository.findAll();
        if (startDate != null) {
            ratings = ratings.stream().filter(r -> !r.getRatingDate().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            ratings = ratings.stream().filter(r -> !r.getRatingDate().isAfter(endDate)).collect(Collectors.toList());
        }

        if (ratings.isEmpty()) {
            return SupplierPerformanceAnalyticsResponse.builder()
                    .averageScore(0.0)
                    .highestScore(0.0)
                    .lowestScore(0.0)
                    .totalRatedSuppliers(0)
                    .build();
        }

        double sum = 0.0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        Set<Long> ratedSupplierIds = new HashSet<>();

        for (SupplierPerformanceRating r : ratings) {
            sum += r.getScore();
            if (r.getScore() > highest) highest = r.getScore();
            if (r.getScore() < lowest) lowest = r.getScore();
            ratedSupplierIds.add(r.getSupplier().getId());
        }

        double avg = round(sum / ratings.size(), 2);
        highest = round(highest, 2);
        lowest = round(lowest, 2);

        return SupplierPerformanceAnalyticsResponse.builder()
                .averageScore(avg)
                .highestScore(highest)
                .lowestScore(lowest)
                .totalRatedSuppliers(ratedSupplierIds.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingDistributionResponse> getRatingDistribution() {
        List<SupplierPerformanceRating> latestRatings = getLatestRatingsForDistinctSuppliers();
        long totalRated = latestRatings.size();

        Map<SupplierRating, Long> counts = new EnumMap<>(SupplierRating.class);
        for (SupplierRating r : SupplierRating.values()) {
            counts.put(r, 0L);
        }

        for (SupplierPerformanceRating r : latestRatings) {
            counts.put(r.getRating(), counts.get(r.getRating()) + 1);
        }

        List<RatingDistributionResponse> result = new ArrayList<>();
        for (SupplierRating rating : SupplierRating.values()) {
            long count = counts.get(rating);
            double percentage = 0.0;
            if (totalRated > 0) {
                percentage = BigDecimal.valueOf((double) count / totalRated * 100.0)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            result.add(RatingDistributionResponse.builder()
                    .rating(rating)
                    .ratingDisplayName(rating.getDisplayName())
                    .count(count)
                    .percentage(percentage)
                    .build());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceStatusDistributionResponse> getPerformanceStatusDistribution() {
        List<SupplierPerformanceRating> latestRatings = getLatestRatingsForDistinctSuppliers();
        long totalRated = latestRatings.size();

        Map<PerformanceStatus, Long> counts = new EnumMap<>(PerformanceStatus.class);
        for (PerformanceStatus s : PerformanceStatus.values()) {
            counts.put(s, 0L);
        }

        for (SupplierPerformanceRating r : latestRatings) {
            counts.put(r.getPerformanceStatus(), counts.get(r.getPerformanceStatus()) + 1);
        }

        List<PerformanceStatusDistributionResponse> result = new ArrayList<>();
        for (PerformanceStatus status : PerformanceStatus.values()) {
            long count = counts.get(status);
            double percentage = 0.0;
            if (totalRated > 0) {
                percentage = BigDecimal.valueOf((double) count / totalRated * 100.0)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            result.add(PerformanceStatusDistributionResponse.builder()
                    .performanceStatus(status)
                    .performanceStatusDisplayName(status.getDisplayName())
                    .count(count)
                    .percentage(percentage)
                    .build());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSupplierResponse> getTopPerformingSuppliers(int limit) {
        validateLimit(limit);
        List<SupplierPerformanceRating> latestRatings = getLatestRatingsForDistinctSuppliers();

        return latestRatings.stream()
                .sorted(Comparator.comparing(SupplierPerformanceRating::getScore).reversed())
                .limit(limit)
                .map(this::mapToTopSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSupplierResponse> getLowPerformingSuppliers(int limit) {
        validateLimit(limit);
        List<SupplierPerformanceRating> latestRatings = getLatestRatingsForDistinctSuppliers();

        return latestRatings.stream()
                .sorted(Comparator.comparing(SupplierPerformanceRating::getScore))
                .limit(limit)
                .map(this::mapToTopSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentEvaluationResponse> getRecentEvaluations(int limit) {
        validateLimit(limit);
        List<SupplierEvaluation> evals = evaluationRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "evaluationDate", "createdAt"))
        ).getContent();

        return evals.stream()
                .map(e -> RecentEvaluationResponse.builder()
                        .evaluationId(e.getId())
                        .evaluationCode(e.getEvaluationCode())
                        .supplierId(e.getSupplier().getId())
                        .supplierName(e.getSupplier().getName())
                        .evaluatorName(e.getEvaluator() != null ? e.getEvaluator().getFullName() : "N/A")
                        .evaluationDate(e.getEvaluationDate())
                        .status(e.getStatus())
                        .totalScore(e.getTotalWeightedScore())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceTrendResponse> getSupplierPerformanceTrends(Long supplierId) {
        if (supplierId == null || !supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", "id", supplierId);
        }

        List<SupplierPerformanceRating> history = performanceRatingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId);
        List<PerformanceTrendResponse> result = history.stream()
                .sorted(Comparator.comparing(SupplierPerformanceRating::getRatingDate))
                .map(r -> PerformanceTrendResponse.builder()
                        .date(r.getRatingDate())
                        .score(r.getScore())
                        .rating(r.getRating())
                        .ratingDisplayName(r.getRating().getDisplayName())
                        .performanceStatus(r.getPerformanceStatus())
                        .performanceStatusDisplayName(r.getPerformanceStatus().getDisplayName())
                        .build())
                .collect(Collectors.toList());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OverallPerformanceTrendResponse> getOverallPerformanceTrend(String groupBy) {
        String mode = (groupBy != null && !groupBy.trim().isEmpty()) ? groupBy.trim().toUpperCase() : "MONTH";
        if (!mode.equals("MONTH") && !mode.equals("QUARTER") && !mode.equals("YEAR")) {
            throw new BadRequestException("Unsupported grouping value: " + groupBy + ". Allowed values: MONTH, QUARTER, YEAR");
        }

        List<SupplierPerformanceRating> allRatings = performanceRatingRepository.findAll();
        if (allRatings.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<Double>> groupedScores = new TreeMap<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter yearFmt = DateTimeFormatter.ofPattern("yyyy");

        for (SupplierPerformanceRating r : allRatings) {
            LocalDate date = r.getRatingDate();
            String key;
            if (mode.equals("YEAR")) {
                key = date.format(yearFmt);
            } else if (mode.equals("QUARTER")) {
                int quarter = (date.getMonthValue() - 1) / 3 + 1;
                key = date.getYear() + "-Q" + quarter;
            } else {
                key = date.format(monthFmt);
            }
            groupedScores.computeIfAbsent(key, k -> new ArrayList<>()).add(r.getScore());
        }

        List<OverallPerformanceTrendResponse> trendList = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : groupedScores.entrySet()) {
            List<Double> scores = entry.getValue();
            double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            trendList.add(OverallPerformanceTrendResponse.builder()
                    .period(entry.getKey())
                    .averageScore(round(avg, 2))
                    .evaluationCount(scores.size())
                    .build());
        }
        return trendList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySupplierStatisticsResponse> getSuppliersByCategory() {
        List<SupplierCategory> categories = categoryRepository.findAll();
        List<CategorySupplierStatisticsResponse> stats = new ArrayList<>();

        for (SupplierCategory cat : categories) {
            long count = supplierRepository.countByCategoryId(cat.getId());
            stats.add(CategorySupplierStatisticsResponse.builder()
                    .categoryId(cat.getId())
                    .categoryName(cat.getName())
                    .supplierCount(count)
                    .build());
        }
        return stats;
    }

    private List<SupplierPerformanceRating> getLatestRatingsForDistinctSuppliers() {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<SupplierPerformanceRating> list = new ArrayList<>();
        for (Supplier s : allSuppliers) {
            Optional<SupplierPerformanceRating> latestOpt = performanceRatingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(s.getId());
            latestOpt.ifPresent(list::add);
        }
        return list;
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new BadRequestException("Limit must be between 1 and 100");
        }
    }

    private TopSupplierResponse mapToTopSupplierResponse(SupplierPerformanceRating r) {
        return TopSupplierResponse.builder()
                .supplierId(r.getSupplier().getId())
                .supplierCode(r.getSupplier().getSupplierCode())
                .supplierName(r.getSupplier().getName())
                .categoryName(r.getSupplier().getCategory() != null ? r.getSupplier().getCategory().getName() : "N/A")
                .latestScore(r.getScore())
                .rating(r.getRating())
                .ratingDisplayName(r.getRating().getDisplayName())
                .performanceStatus(r.getPerformanceStatus())
                .performanceStatusDisplayName(r.getPerformanceStatus().getDisplayName())
                .ratingDate(r.getRatingDate())
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

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
