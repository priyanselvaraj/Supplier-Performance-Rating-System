package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.SupplierRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.SupplierResponse;
import com.supplier.sprsystem.model.entity.SupplierStatus;
import com.supplier.sprsystem.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier Management", description = "Supplier CRUD, search, filter, pagination, and status operations")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "Get paginated, filtered, searched, and sorted suppliers (Admin & Manager)")
    public ResponseEntity<ApiResponse<PaginatedResponse<SupplierResponse>>> getSuppliers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) SupplierStatus status,
            @RequestParam(required = false) com.supplier.sprsystem.model.entity.RatingCategory ratingCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PaginatedResponse<SupplierResponse> response = supplierService.getSuppliers(
                keyword, categoryId, status, ratingCategory, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers by keyword across name, code, email, and contact person")
    public ResponseEntity<ApiResponse<PaginatedResponse<SupplierResponse>>> searchSuppliers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "supplierName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PaginatedResponse<SupplierResponse> response = supplierService.searchSuppliers(keyword, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Suppliers search results retrieved successfully", response));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter suppliers by category, active status, city, and country")
    public ResponseEntity<ApiResponse<PaginatedResponse<SupplierResponse>>> filterSuppliers(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "supplierName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PaginatedResponse<SupplierResponse> response = supplierService.filterSuppliers(categoryId, active, city, country, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Filtered suppliers retrieved successfully", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all suppliers as a flat list")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        List<SupplierResponse> list = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success("All suppliers retrieved successfully", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable Long id) {
        SupplierResponse supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved successfully", supplier));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get supplier by supplier code")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierByCode(@PathVariable String code) {
        SupplierResponse supplier = supplierService.getSupplierByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved successfully", supplier));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create a new supplier (Admin and Manager)")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody SupplierRequest supplierRequest) {
        SupplierResponse created = supplierService.createSupplier(supplierRequest);
        return new ResponseEntity<>(ApiResponse.success("Supplier created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update supplier details (Admin and Manager)")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest supplierRequest) {
        SupplierResponse updated = supplierService.updateSupplier(id, supplierRequest);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", updated));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Activate supplier (Admin and Manager)")
    public ResponseEntity<ApiResponse<SupplierResponse>> activateSupplier(@PathVariable Long id) {
        SupplierResponse updated = supplierService.activateSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier activated successfully", updated));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Deactivate supplier (Admin and Manager)")
    public ResponseEntity<ApiResponse<SupplierResponse>> deactivateSupplier(@PathVariable Long id) {
        SupplierResponse updated = supplierService.deactivateSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deactivated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update supplier status (ACTIVE, INACTIVE, PENDING_REVIEW) (Admin and Manager)")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplierStatus(
            @PathVariable Long id,
            @RequestParam SupplierStatus status) {
        SupplierResponse updated = supplierService.updateSupplierStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Supplier status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete supplier (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully"));
    }
}
