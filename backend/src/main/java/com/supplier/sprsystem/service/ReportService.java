package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    SupplierPerformanceReportResponse getSupplierPerformanceReport(Long supplierId, LocalDate startDate, LocalDate endDate);

    SupplierEvaluationReportResponse getSupplierEvaluationReport(Long evaluationId);

    List<SupplierPerformanceRatingResponse> getSupplierRatingHistoryReport(Long supplierId, LocalDate startDate, LocalDate endDate);

    OverallPerformanceReportResponse getOverallPerformanceReport(LocalDate startDate, LocalDate endDate);

    EvaluationSummaryReportResponse getEvaluationSummaryReport(LocalDate startDate, LocalDate endDate);

    // Existing methods for backward compatibility
    PerformanceReportResponse generatePerformanceReport(
            String keyword, Long categoryId, SupplierStatus status, RatingCategory ratingCategory, LocalDate startDate, LocalDate endDate
    );

    List<SupplierResponse> getTopPerformingSuppliers(int limit);

    List<SupplierResponse> getLowPerformingSuppliers(int limit);

    List<EvaluationResponse> getSupplierEvaluationHistoryReport(Long supplierId);
}
