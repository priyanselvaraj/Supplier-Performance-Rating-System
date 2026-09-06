package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import com.supplier.sprsystem.service.ReportExportService;
import com.supplier.sprsystem.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Export Module", description = "Supplier performance reports, evaluation scorecards, overall summaries, and PDF/Excel/CSV exports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(ReportService reportService, ReportExportService reportExportService) {
        this.reportService = reportService;
        this.reportExportService = reportExportService;
    }

    // ==========================================
    // 1. JSON REPORT ENDPOINTS
    // ==========================================

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate comprehensive individual supplier performance report")
    public ResponseEntity<ApiResponse<SupplierPerformanceReportResponse>> getSupplierPerformanceReport(
            @PathVariable Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        SupplierPerformanceReportResponse report = reportService.getSupplierPerformanceReport(supplierId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Supplier performance report generated successfully", report));
    }

    @GetMapping("/evaluation/{evaluationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate detailed supplier evaluation scorecard report")
    public ResponseEntity<ApiResponse<SupplierEvaluationReportResponse>> getSupplierEvaluationReport(
            @PathVariable Long evaluationId
    ) {
        SupplierEvaluationReportResponse report = reportService.getSupplierEvaluationReport(evaluationId);
        return ResponseEntity.ok(ApiResponse.success("Supplier evaluation report generated successfully", report));
    }

    @GetMapping("/supplier/{supplierId}/rating-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate supplier rating history report")
    public ResponseEntity<ApiResponse<List<SupplierPerformanceRatingResponse>>> getSupplierRatingHistoryReport(
            @PathVariable Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SupplierPerformanceRatingResponse> history = reportService.getSupplierRatingHistoryReport(supplierId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Supplier rating history report generated successfully", history));
    }

    @GetMapping("/overall")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate overall system supplier performance summary report (Admin & Manager)")
    public ResponseEntity<ApiResponse<OverallPerformanceReportResponse>> getOverallPerformanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OverallPerformanceReportResponse report = reportService.getOverallPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Overall performance report generated successfully", report));
    }

    @GetMapping("/evaluations/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate evaluation summary status report (Admin & Manager)")
    public ResponseEntity<ApiResponse<EvaluationSummaryReportResponse>> getEvaluationSummaryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        EvaluationSummaryReportResponse report = reportService.getEvaluationSummaryReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Evaluation summary report generated successfully", report));
    }

    // ==========================================
    // 2. SUPPLIER REPORT EXPORT ENDPOINTS
    // ==========================================

    @GetMapping("/supplier/{supplierId}/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export individual supplier performance report as PDF")
    public ResponseEntity<byte[]> exportSupplierReportPdf(
            @PathVariable Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] pdfBytes = reportExportService.exportSupplierPerformanceReportPdf(supplierId, startDate, endDate);
        String filename = "supplier-performance-report-" + supplierId + "-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/supplier/{supplierId}/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export individual supplier performance report as Excel (XLSX)")
    public ResponseEntity<byte[]> exportSupplierReportExcel(
            @PathVariable Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] excelBytes = reportExportService.exportSupplierPerformanceReportExcel(supplierId, startDate, endDate);
        String filename = "supplier-performance-report-" + supplierId + "-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/supplier/{supplierId}/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export individual supplier performance report as CSV")
    public ResponseEntity<byte[]> exportSupplierReportCsv(
            @PathVariable Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] csvBytes = reportExportService.exportSupplierPerformanceReportCsv(supplierId, startDate, endDate);
        String filename = "supplier-performance-report-" + supplierId + "-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    // ==========================================
    // 3. OVERALL REPORT EXPORT ENDPOINTS
    // ==========================================

    @GetMapping("/overall/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export overall performance summary report as PDF (Admin & Manager)")
    public ResponseEntity<byte[]> exportOverallReportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] pdfBytes = reportExportService.exportOverallPerformanceReportPdf(startDate, endDate);
        String filename = "overall-performance-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/overall/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export overall performance summary report as Excel (Admin & Manager)")
    public ResponseEntity<byte[]> exportOverallReportExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] excelBytes = reportExportService.exportOverallPerformanceReportExcel(startDate, endDate);
        String filename = "overall-performance-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/overall/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export overall performance summary report as CSV (Admin & Manager)")
    public ResponseEntity<byte[]> exportOverallReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] csvBytes = reportExportService.exportOverallPerformanceReportCsv(startDate, endDate);
        String filename = "overall-performance-report-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    // ==========================================
    // 4. EVALUATION REPORT EXPORT ENDPOINTS
    // ==========================================

    @GetMapping("/evaluation/{evaluationId}/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export supplier evaluation scorecard as PDF")
    public ResponseEntity<byte[]> exportEvaluationReportPdf(@PathVariable Long evaluationId) {
        byte[] pdfBytes = reportExportService.exportSupplierEvaluationReportPdf(evaluationId);
        String filename = "evaluation-scorecard-" + evaluationId + "-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/evaluation/{evaluationId}/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export supplier evaluation scorecard as Excel")
    public ResponseEntity<byte[]> exportEvaluationReportExcel(@PathVariable Long evaluationId) {
        byte[] excelBytes = reportExportService.exportSupplierEvaluationReportExcel(evaluationId);
        String filename = "evaluation-scorecard-" + evaluationId + "-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/evaluation/{evaluationId}/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export supplier evaluation scorecard as CSV")
    public ResponseEntity<byte[]> exportEvaluationReportCsv(@PathVariable Long evaluationId) {
        byte[] csvBytes = reportExportService.exportSupplierEvaluationReportCsv(evaluationId);
        String filename = "evaluation-scorecard-" + evaluationId + "-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    // ==========================================
    // 5. BACKWARD COMPATIBILITY ENDPOINTS
    // ==========================================

    @GetMapping("/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate filtered supplier performance ledger report")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getPerformanceReport(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) SupplierStatus status,
            @RequestParam(required = false) RatingCategory ratingCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PerformanceReportResponse report = reportService.generatePerformanceReport(
                keyword, categoryId, status, ratingCategory, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Performance report generated successfully", report));
    }

    @GetMapping("/top-suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get top performing suppliers ranked by rating score")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getTopSuppliers(
            @RequestParam(defaultValue = "5") int limit) {
        List<SupplierResponse> topSuppliers = reportService.getTopPerformingSuppliers(limit);
        return ResponseEntity.ok(ApiResponse.success("Top suppliers fetched successfully", topSuppliers));
    }

    @GetMapping("/low-suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get low performing suppliers requiring attention")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getLowSuppliers(
            @RequestParam(defaultValue = "5") int limit) {
        List<SupplierResponse> lowSuppliers = reportService.getLowPerformingSuppliers(limit);
        return ResponseEntity.ok(ApiResponse.success("Low performing suppliers fetched successfully", lowSuppliers));
    }

    @GetMapping("/supplier/{supplierId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get evaluation history report for an individual supplier")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getSupplierEvaluationHistory(
            @PathVariable Long supplierId) {
        List<EvaluationResponse> history = reportService.getSupplierEvaluationHistoryReport(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier evaluation history report generated", history));
    }
}
