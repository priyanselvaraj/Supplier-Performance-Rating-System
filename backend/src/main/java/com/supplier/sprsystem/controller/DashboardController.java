package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.service.DashboardAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard & Analytics", description = "Executive KPI metrics, supplier statistics, rating distributions, trends, and top/low performers")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardAnalyticsService analyticsService;

    public DashboardController(DashboardAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get overall dashboard executive KPI summary (Admin & Manager)")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        DashboardSummaryResponse summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary fetched successfully", summary));
    }

    @GetMapping("/supplier-statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get supplier counts and category breakdowns (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierStatisticsResponse>> getSupplierStatistics() {
        SupplierStatisticsResponse statistics = analyticsService.getSupplierStatistics();
        return ResponseEntity.ok(ApiResponse.success("Supplier statistics fetched successfully", statistics));
    }

    @GetMapping("/evaluation-statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get evaluation lifecycle status counts (Admin & Manager)")
    public ResponseEntity<ApiResponse<EvaluationStatisticsResponse>> getEvaluationStatistics() {
        EvaluationStatisticsResponse statistics = analyticsService.getEvaluationStatistics();
        return ResponseEntity.ok(ApiResponse.success("Evaluation statistics fetched successfully", statistics));
    }

    @GetMapping("/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get performance score metrics with optional date range filter (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierPerformanceAnalyticsResponse>> getPerformanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        SupplierPerformanceAnalyticsResponse analytics = analyticsService.getPerformanceAnalytics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Performance analytics fetched successfully", analytics));
    }

    @GetMapping("/rating-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get rating level counts and percentage distribution (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<RatingDistributionResponse>>> getRatingDistribution() {
        List<RatingDistributionResponse> distribution = analyticsService.getRatingDistribution();
        return ResponseEntity.ok(ApiResponse.success("Rating distribution fetched successfully", distribution));
    }

    @GetMapping("/performance-status-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get performance status breakdown and percentages (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<PerformanceStatusDistributionResponse>>> getPerformanceStatusDistribution() {
        List<PerformanceStatusDistributionResponse> distribution = analyticsService.getPerformanceStatusDistribution();
        return ResponseEntity.ok(ApiResponse.success("Performance status distribution fetched successfully", distribution));
    }

    @GetMapping("/top-suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get highest-performing suppliers ordered by score (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<TopSupplierResponse>>> getTopPerformingSuppliers(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<TopSupplierResponse> topSuppliers = analyticsService.getTopPerformingSuppliers(limit);
        return ResponseEntity.ok(ApiResponse.success("Top performing suppliers fetched successfully", topSuppliers));
    }

    @GetMapping("/low-performing-suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get lowest-performing suppliers requiring attention (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<TopSupplierResponse>>> getLowPerformingSuppliers(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<TopSupplierResponse> lowSuppliers = analyticsService.getLowPerformingSuppliers(limit);
        return ResponseEntity.ok(ApiResponse.success("Low performing suppliers fetched successfully", lowSuppliers));
    }

    @GetMapping("/recent-evaluations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get recent evaluation audit log entries (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<RecentEvaluationResponse>>> getRecentEvaluations(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecentEvaluationResponse> recentEvals = analyticsService.getRecentEvaluations(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent evaluations fetched successfully", recentEvals));
    }

    @GetMapping("/performance-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get historical performance timeline for a specific supplier (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<PerformanceTrendResponse>>> getSupplierPerformanceTrends(
            @RequestParam Long supplierId
    ) {
        List<PerformanceTrendResponse> trends = analyticsService.getSupplierPerformanceTrends(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier performance trends fetched successfully", trends));
    }

    @GetMapping("/overall-performance-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get aggregated system-wide score trend grouped by period (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<OverallPerformanceTrendResponse>>> getOverallPerformanceTrend(
            @RequestParam(defaultValue = "MONTH") String groupBy
    ) {
        List<OverallPerformanceTrendResponse> trends = analyticsService.getOverallPerformanceTrend(groupBy);
        return ResponseEntity.ok(ApiResponse.success("Overall performance trend fetched successfully", trends));
    }

    @GetMapping("/suppliers-by-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get supplier count aggregated by category for chart visualizations (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<CategorySupplierStatisticsResponse>>> getSuppliersByCategory() {
        List<CategorySupplierStatisticsResponse> stats = analyticsService.getSuppliersByCategory();
        return ResponseEntity.ok(ApiResponse.success("Suppliers by category fetched successfully", stats));
    }
}
