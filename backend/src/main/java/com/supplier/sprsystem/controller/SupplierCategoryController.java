package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.SupplierCategoryRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.SupplierCategoryResponse;
import com.supplier.sprsystem.service.SupplierCategoryService;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Supplier Categories", description = "Supplier category management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class SupplierCategoryController {

    private final SupplierCategoryService categoryService;

    public SupplierCategoryController(SupplierCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all supplier categories (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<SupplierCategoryResponse>>> getAllCategories() {
        List<SupplierCategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Supplier categories fetched successfully", categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier category by ID (Admin & Manager)")
    public ResponseEntity<ApiResponse<SupplierCategoryResponse>> getCategoryById(@PathVariable Long id) {
        SupplierCategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new supplier category (Admin only)")
    public ResponseEntity<ApiResponse<SupplierCategoryResponse>> createCategory(
            @Valid @RequestBody SupplierCategoryRequest categoryRequest) {
        SupplierCategoryResponse category = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(ApiResponse.success("Category created successfully", category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update supplier category (Admin only)")
    public ResponseEntity<ApiResponse<SupplierCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody SupplierCategoryRequest categoryRequest) {
        SupplierCategoryResponse category = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete supplier category safely (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate supplier category (Admin only)")
    public ResponseEntity<ApiResponse<SupplierCategoryResponse>> activateCategory(@PathVariable Long id) {
        SupplierCategoryResponse category = categoryService.activateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category activated successfully", category));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate supplier category (Admin only)")
    public ResponseEntity<ApiResponse<SupplierCategoryResponse>> deactivateCategory(@PathVariable Long id) {
        SupplierCategoryResponse category = categoryService.deactivateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deactivated successfully", category));
    }
}
