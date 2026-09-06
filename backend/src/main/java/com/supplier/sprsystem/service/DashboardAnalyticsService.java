package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardAnalyticsService extends DashboardService {

    @Override
    DashboardSummaryResponse getDashboardSummary();

    SupplierStatisticsResponse getSupplierStatistics();

    EvaluationStatisticsResponse getEvaluationStatistics();

    SupplierPerformanceAnalyticsResponse getPerformanceAnalytics(LocalDate startDate, LocalDate endDate);

    List<RatingDistributionResponse> getRatingDistribution();

    List<PerformanceStatusDistributionResponse> getPerformanceStatusDistribution();

    List<TopSupplierResponse> getTopPerformingSuppliers(int limit);

    List<TopSupplierResponse> getLowPerformingSuppliers(int limit);

    List<RecentEvaluationResponse> getRecentEvaluations(int limit);

    List<PerformanceTrendResponse> getSupplierPerformanceTrends(Long supplierId);

    List<OverallPerformanceTrendResponse> getOverallPerformanceTrend(String groupBy);

    List<CategorySupplierStatisticsResponse> getSuppliersByCategory();
}
