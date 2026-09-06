package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.RatingHistoryResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceRatingResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceSummaryResponse;
import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierEvaluation;
import com.supplier.sprsystem.model.entity.SupplierPerformanceRating;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;
import java.util.List;

public interface SupplierRatingService {

    SupplierPerformanceRatingResponse generateRatingForEvaluation(SupplierEvaluation evaluation);

    SupplierPerformanceRatingResponse generateRatingForEvaluationId(Long evaluationId);

    SupplierPerformanceRatingResponse getLatestSupplierRating(Long supplierId);

    RatingHistoryResponse getSupplierRatingHistory(Long supplierId);

    SupplierPerformanceSummaryResponse getSupplierPerformanceSummary(Long supplierId);

    PaginatedResponse<SupplierPerformanceRatingResponse> getAllRatings(
            Long supplierId, SupplierRating rating, PerformanceStatus performanceStatus,
            LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String direction
    );

    SupplierPerformanceRatingResponse getRatingById(Long id);

    List<SupplierPerformanceRatingResponse> getHighPerformingSuppliers();

    List<SupplierPerformanceRatingResponse> getSuppliersNeedingImprovement();

    SupplierRating calculateRating(double score);

    PerformanceStatus calculatePerformanceStatus(double score);
}
