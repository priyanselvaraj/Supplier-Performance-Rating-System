package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.ReportBuilderRequest;
import com.supplier.sprsystem.dto.request.SavedReportRequest;
import com.supplier.sprsystem.dto.request.SupplierComparisonRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface BusinessIntelligenceService {

    BiDashboardResponse getBiDashboardSummary();

    List<OverallPerformanceTrendResponse> getPerformanceTrends(String timeUnit, Long supplierId, Long categoryId, LocalDate startDate, LocalDate endDate);

    SupplierComparisonResponse compareSuppliers(SupplierComparisonRequest request);

    BenchmarkResponse getSupplierBenchmark(Long supplierId);

    ReportPreviewResponse generateReportPreview(ReportBuilderRequest request);

    SavedReportResponse saveReport(SavedReportRequest request, User currentUser);

    List<SavedReportResponse> getSavedReports(User currentUser);

    SavedReportResponse getSavedReportById(Long id, User currentUser);

    SavedReportResponse updateSavedReport(Long id, SavedReportRequest request, User currentUser);

    void deleteSavedReport(Long id, User currentUser);

    ExecutiveDashboardResponse getExecutiveDashboard();
}
