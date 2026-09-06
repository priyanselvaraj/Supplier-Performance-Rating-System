package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.RatingHistoryResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceRatingResponse;
import com.supplier.sprsystem.dto.response.SupplierPerformanceSummaryResponse;
import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierRating;
import com.supplier.sprsystem.service.SupplierRatingService;
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
@RequestMapping("/api/v1/ratings")
@Tag(name = "Supplier Ratings", description = "Performance rating classification, history tracking, trends, and supplier performance summaries")
@SecurityRequirement(name = "bearerAuth")
public class SupplierRatingController {

    private final SupplierRatingService ratingService;

    public SupplierRatingController(SupplierRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all supplier ratings with pagination, sorting, and filters (Admin & Manager)")
    public ResponseEntity<ApiResponse<PaginatedResponse<SupplierPerformanceRatingResponse>>> getAllRatings(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) SupplierRating rating,
            @RequestParam(required = false) PerformanceStatus performanceStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ratingDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PaginatedResponse<SupplierPerformanceRatingResponse> response = ratingService.getAllRatings(
                supplierId, rating, performanceStatus, startDate, endDate, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Supplier ratings fetched successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get rating by ID (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierPerformanceRatingResponse>> getRatingById(@PathVariable Long id) {
        SupplierPerformanceRatingResponse response = ratingService.getRatingById(id);
        return ResponseEntity.ok(ApiResponse.success("Rating fetched successfully", response));
    }

    @GetMapping("/supplier/{supplierId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get latest performance rating for a supplier (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierPerformanceRatingResponse>> getLatestSupplierRating(@PathVariable Long supplierId) {
        SupplierPerformanceRatingResponse response = ratingService.getLatestSupplierRating(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Latest supplier rating fetched successfully", response));
    }

    @GetMapping("/supplier/{supplierId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get historical performance ratings for a supplier (Admin & Manager)")
    public ResponseEntity<ApiResponse<RatingHistoryResponse>> getSupplierRatingHistory(@PathVariable Long supplierId) {
        RatingHistoryResponse response = ratingService.getSupplierRatingHistory(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier rating history fetched successfully", response));
    }

    @GetMapping("/supplier/{supplierId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get supplier performance summary with score differences and performance trend (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierPerformanceSummaryResponse>> getSupplierPerformanceSummary(@PathVariable Long supplierId) {
        SupplierPerformanceSummaryResponse response = ratingService.getSupplierPerformanceSummary(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier performance summary fetched successfully", response));
    }

    @GetMapping("/high-performing")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all suppliers classified as HIGH_PERFORMING (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<SupplierPerformanceRatingResponse>>> getHighPerformingSuppliers() {
        List<SupplierPerformanceRatingResponse> response = ratingService.getHighPerformingSuppliers();
        return ResponseEntity.ok(ApiResponse.success("High performing suppliers fetched successfully", response));
    }

    @GetMapping("/needs-improvement")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all suppliers classified as NEEDS_IMPROVEMENT or LOW_PERFORMING (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<SupplierPerformanceRatingResponse>>> getSuppliersNeedingImprovement() {
        List<SupplierPerformanceRatingResponse> response = ratingService.getSuppliersNeedingImprovement();
        return ResponseEntity.ok(ApiResponse.success("Suppliers needing improvement fetched successfully", response));
    }
}
