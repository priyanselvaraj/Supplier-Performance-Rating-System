package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.ReportBuilderRequest;
import com.supplier.sprsystem.dto.request.SavedReportRequest;
import com.supplier.sprsystem.dto.request.SupplierComparisonRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.BusinessIntelligenceService;
import com.supplier.sprsystem.service.KpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bi")
@Tag(name = "Business Intelligence & Analytics", description = "Enterprise BI, KPI calculations, benchmarking, supplier comparisons, and saved reports")
@SecurityRequirement(name = "bearerAuth")
public class BusinessIntelligenceController {

    private final BusinessIntelligenceService biService;
    private final KpiService kpiService;
    private final UserRepository userRepository;

    public BusinessIntelligenceController(BusinessIntelligenceService biService, KpiService kpiService, UserRepository userRepository) {
        this.biService = biService;
        this.kpiService = kpiService;
        this.userRepository = userRepository;
    }

    private User resolveCurrentUser(UserDetails userDetails) {
        if (userDetails != null) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
        }
        return userRepository.findAll().stream().findFirst().orElse(null);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get executive BI dashboard summary with real KPIs and analytics")
    public ResponseEntity<ApiResponse<BiDashboardResponse>> getBiDashboard() {
        BiDashboardResponse response = biService.getBiDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("BI dashboard summary retrieved successfully", response));
    }

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Calculate and retrieve all active KPI results with status evaluations")
    public ResponseEntity<ApiResponse<List<KpiCalculationResultResponse>>> getCalculatedKpis() {
        List<KpiCalculationResultResponse> response = kpiService.calculateAllActiveKpis();
        return ResponseEntity.ok(ApiResponse.success("KPI calculations evaluated successfully", response));
    }

    @GetMapping("/kpis/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Calculate and retrieve a specific KPI result by ID")
    public ResponseEntity<ApiResponse<KpiCalculationResultResponse>> getCalculatedKpiById(@PathVariable Long id) {
        KpiCalculationResultResponse response = kpiService.calculateKpiById(id);
        return ResponseEntity.ok(ApiResponse.success("KPI calculated successfully", response));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Retrieve multi-granularity performance trends (Weekly, Monthly, Quarterly, Yearly)")
    public ResponseEntity<ApiResponse<List<OverallPerformanceTrendResponse>>> getPerformanceTrends(
            @RequestParam(defaultValue = "MONTH") String timeUnit,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<OverallPerformanceTrendResponse> response = biService.getPerformanceTrends(timeUnit, supplierId, categoryId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Performance trends retrieved successfully", response));
    }

    @GetMapping("/supplier-comparison")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Side-by-side comparison of multiple suppliers across performance dimensions")
    public ResponseEntity<ApiResponse<SupplierComparisonResponse>> compareSuppliersGet(
            @RequestParam List<Long> supplierIds
    ) {
        SupplierComparisonRequest request = SupplierComparisonRequest.builder()
                .supplierIds(supplierIds)
                .build();
        SupplierComparisonResponse response = biService.compareSuppliers(request);
        return ResponseEntity.ok(ApiResponse.success("Supplier comparison generated successfully", response));
    }

    @PostMapping("/supplier-comparison")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Side-by-side comparison of multiple suppliers (POST)")
    public ResponseEntity<ApiResponse<SupplierComparisonResponse>> compareSuppliersPost(
            @Valid @RequestBody SupplierComparisonRequest request
    ) {
        SupplierComparisonResponse response = biService.compareSuppliers(request);
        return ResponseEntity.ok(ApiResponse.success("Supplier comparison generated successfully", response));
    }

    @GetMapping("/benchmarks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Compare supplier performance against Category and Overall benchmarks")
    public ResponseEntity<ApiResponse<BenchmarkResponse>> getSupplierBenchmark(
            @RequestParam Long supplierId
    ) {
        BenchmarkResponse response = biService.getSupplierBenchmark(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier benchmark analysis retrieved successfully", response));
    }

    @PostMapping("/reports/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate interactive report preview with custom filters and chart data")
    public ResponseEntity<ApiResponse<ReportPreviewResponse>> generateReportPreview(
            @RequestBody ReportBuilderRequest request
    ) {
        ReportPreviewResponse response = biService.generateReportPreview(request);
        return ResponseEntity.ok(ApiResponse.success("Report preview generated successfully", response));
    }

    @PostMapping("/reports/saved")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Save a custom report configuration")
    public ResponseEntity<ApiResponse<SavedReportResponse>> saveReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SavedReportRequest request
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        SavedReportResponse response = biService.saveReport(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Report configuration saved successfully", response));
    }

    @GetMapping("/reports/saved")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List accessible saved report configurations")
    public ResponseEntity<ApiResponse<List<SavedReportResponse>>> getSavedReports(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        List<SavedReportResponse> response = biService.getSavedReports(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Saved reports retrieved successfully", response));
    }

    @GetMapping("/reports/saved/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get a specific saved report configuration")
    public ResponseEntity<ApiResponse<SavedReportResponse>> getSavedReportById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        SavedReportResponse response = biService.getSavedReportById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Saved report retrieved successfully", response));
    }

    @PutMapping("/reports/saved/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a saved report configuration")
    public ResponseEntity<ApiResponse<SavedReportResponse>> updateSavedReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody SavedReportRequest request
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        SavedReportResponse response = biService.updateSavedReport(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Saved report updated successfully", response));
    }

    @DeleteMapping("/reports/saved/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a saved report configuration")
    public ResponseEntity<ApiResponse<Void>> deleteSavedReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        biService.deleteSavedReport(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Saved report deleted successfully", null));
    }

    @GetMapping({"/executive", "/executive/dashboard"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get high-level executive C-suite dashboard metrics")
    public ResponseEntity<ApiResponse<ExecutiveDashboardResponse>> getExecutiveDashboard() {
        ExecutiveDashboardResponse response = biService.getExecutiveDashboard();
        return ResponseEntity.ok(ApiResponse.success("Executive dashboard retrieved successfully", response));
    }
}
