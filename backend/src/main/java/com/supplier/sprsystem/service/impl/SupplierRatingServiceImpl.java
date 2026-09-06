package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.RatingHistoryResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceRatingResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceSummaryResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.SupplierEvaluationRepository;
import com.supplier.sprsystem.repository.SupplierPerformanceRatingRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.service.SupplierRatingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierRatingServiceImpl implements SupplierRatingService {

    private final SupplierPerformanceRatingRepository ratingRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierRepository supplierRepository;

    public SupplierRatingServiceImpl(SupplierPerformanceRatingRepository ratingRepository,
                                     SupplierEvaluationRepository evaluationRepository,
                                     SupplierRepository supplierRepository) {
        this.ratingRepository = ratingRepository;
        this.evaluationRepository = evaluationRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public SupplierPerformanceRatingResponse generateRatingForEvaluationId(Long evaluationId) {
        SupplierEvaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierEvaluation", "id", evaluationId));
        return generateRatingForEvaluation(evaluation);
    }

    @Override
    @Transactional
    public SupplierPerformanceRatingResponse generateRatingForEvaluation(SupplierEvaluation evaluation) {
        if (evaluation.getStatus() != EvaluationStatus.COMPLETED && evaluation.getStatus() != EvaluationStatus.SUBMITTED) {
            throw new BadRequestException("Evaluation must be completed before generating a rating. Current status: " + evaluation.getStatus());
        }

        double score = evaluation.getTotalWeightedScore();
        if (score < 0.0 || score > 100.0) {
            throw new BadRequestException(String.format("Evaluation score must be between 0 and 100. Provided score: %.2f", score));
        }

        SupplierRating rating = calculateRating(score);
        PerformanceStatus performanceStatus = calculatePerformanceStatus(score);
        LocalDate ratingDate = evaluation.getEvaluationDate() != null ? evaluation.getEvaluationDate() : LocalDate.now();

        Optional<SupplierPerformanceRating> existingRatingOpt = ratingRepository.findByEvaluationId(evaluation.getId());
        SupplierPerformanceRating ratingEntity;

        if (existingRatingOpt.isPresent()) {
            ratingEntity = existingRatingOpt.get();
            ratingEntity.setScore(score);
            ratingEntity.setRating(rating);
            ratingEntity.setPerformanceStatus(performanceStatus);
            ratingEntity.setRatingDate(ratingDate);
        } else {
            ratingEntity = SupplierPerformanceRating.builder()
                    .supplier(evaluation.getSupplier())
                    .evaluation(evaluation)
                    .score(score)
                    .rating(rating)
                    .performanceStatus(performanceStatus)
                    .ratingDate(ratingDate)
                    .build();
        }

        SupplierPerformanceRating saved = ratingRepository.save(ratingEntity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceRatingResponse getLatestSupplierRating(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", "id", supplierId);
        }

        SupplierPerformanceRating rating = ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierPerformanceRating", "supplierId", supplierId));

        return mapToResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingHistoryResponse getSupplierRatingHistory(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        List<SupplierPerformanceRating> historyList = ratingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId);
        List<SupplierPerformanceRatingResponse> responseList = historyList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return RatingHistoryResponse.builder()
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .ratings(responseList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceSummaryResponse getSupplierPerformanceSummary(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", supplierId));

        List<SupplierPerformanceRating> historyList = ratingRepository.findBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplierId);

        if (historyList.isEmpty()) {
            return SupplierPerformanceSummaryResponse.builder()
                    .supplierId(supplier.getId())
                    .supplierCode(supplier.getSupplierCode())
                    .supplierName(supplier.getName())
                    .latestScore(null)
                    .latestRating(null)
                    .latestPerformanceStatus(null)
                    .previousScore(null)
                    .previousRating(null)
                    .scoreDifference(null)
                    .performanceTrend(PerformanceTrend.INSUFFICIENT_DATA)
                    .ratingDate(null)
                    .totalEvaluations(0)
                    .build();
        }

        SupplierPerformanceRating latest = historyList.get(0);
        Double previousScore = null;
        SupplierRating previousRating = null;
        Double scoreDiff = null;

        if (historyList.size() > 1) {
            SupplierPerformanceRating previous = historyList.get(1);
            previousScore = previous.getScore();
            previousRating = previous.getRating();
            scoreDiff = round(latest.getScore() - previousScore, 2);
        }

        PerformanceTrend trend = PerformanceTrend.calculateTrend(latest.getScore(), previousScore);

        return SupplierPerformanceSummaryResponse.builder()
                .supplierId(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .supplierName(supplier.getName())
                .latestScore(latest.getScore())
                .latestRating(latest.getRating())
                .latestPerformanceStatus(latest.getPerformanceStatus())
                .previousScore(previousScore)
                .previousRating(previousRating)
                .scoreDifference(scoreDiff)
                .performanceTrend(trend)
                .ratingDate(latest.getRatingDate())
                .totalEvaluations(historyList.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SupplierPerformanceRatingResponse> getAllRatings(
            Long supplierId, SupplierRating rating, PerformanceStatus performanceStatus,
            LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<SupplierPerformanceRating> ratingPage = ratingRepository.filterRatings(
                supplierId, rating, performanceStatus, startDate, endDate, pageable);
        return PaginatedResponse.fromPage(ratingPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceRatingResponse getRatingById(Long id) {
        SupplierPerformanceRating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierPerformanceRating", "id", id));
        return mapToResponse(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierPerformanceRatingResponse> getHighPerformingSuppliers() {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<SupplierPerformanceRatingResponse> result = new ArrayList<>();

        for (Supplier supplier : allSuppliers) {
            Optional<SupplierPerformanceRating> latestOpt = ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplier.getId());
            if (latestOpt.isPresent()) {
                SupplierPerformanceRating latest = latestOpt.get();
                if (latest.getPerformanceStatus() == PerformanceStatus.HIGH_PERFORMING) {
                    result.add(mapToResponse(latest));
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierPerformanceRatingResponse> getSuppliersNeedingImprovement() {
        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<SupplierPerformanceRatingResponse> result = new ArrayList<>();

        for (Supplier supplier : allSuppliers) {
            Optional<SupplierPerformanceRating> latestOpt = ratingRepository.findFirstBySupplierIdOrderByRatingDateDescCreatedAtDesc(supplier.getId());
            if (latestOpt.isPresent()) {
                SupplierPerformanceRating latest = latestOpt.get();
                if (latest.getPerformanceStatus() == PerformanceStatus.NEEDS_IMPROVEMENT || latest.getPerformanceStatus() == PerformanceStatus.LOW_PERFORMING) {
                    result.add(mapToResponse(latest));
                }
            }
        }
        return result;
    }

    @Override
    public SupplierRating calculateRating(double score) {
        if (score < 0.0 || score > 100.0) {
            throw new BadRequestException("Score must be between 0 and 100. Given score: " + score);
        }
        return SupplierRating.fromScore(score);
    }

    @Override
    public PerformanceStatus calculatePerformanceStatus(double score) {
        if (score < 0.0 || score > 100.0) {
            throw new BadRequestException("Score must be between 0 and 100. Given score: " + score);
        }
        return PerformanceStatus.fromScore(score);
    }

    private SupplierPerformanceRatingResponse mapToResponse(SupplierPerformanceRating entity) {
        return SupplierPerformanceRatingResponse.builder()
                .id(entity.getId())
                .supplierId(entity.getSupplier().getId())
                .supplierCode(entity.getSupplier().getSupplierCode())
                .supplierName(entity.getSupplier().getName())
                .evaluationId(entity.getEvaluation() != null ? entity.getEvaluation().getId() : null)
                .evaluationCode(entity.getEvaluation() != null ? entity.getEvaluation().getEvaluationCode() : null)
                .score(entity.getScore())
                .rating(entity.getRating())
                .performanceStatus(entity.getPerformanceStatus())
                .ratingDate(entity.getRatingDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        String cleanSortBy = "ratingDate";
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String prop = sortBy.trim();
            if (prop.equalsIgnoreCase("id")) {
                cleanSortBy = "id";
            } else if (prop.equalsIgnoreCase("score")) {
                cleanSortBy = "score";
            } else if (prop.equalsIgnoreCase("ratingDate") || prop.equalsIgnoreCase("date")) {
                cleanSortBy = "ratingDate";
            } else if (prop.equalsIgnoreCase("createdAt")) {
                cleanSortBy = "createdAt";
            } else {
                cleanSortBy = prop;
            }
        }

        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction != null && direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        }

        return PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(sortDirection, cleanSortBy));
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
