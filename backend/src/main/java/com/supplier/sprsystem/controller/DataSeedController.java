package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.config.DataInitializer;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.repository.SupplierCategoryRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/seed")
@Tag(name = "Data Seeding", description = "Endpoints for seeding enterprise sample data")
@SecurityRequirement(name = "bearerAuth")
public class DataSeedController {

    private final DataInitializer dataInitializer;
    private final SupplierRepository supplierRepository;
    private final SupplierCategoryRepository categoryRepository;

    public DataSeedController(DataInitializer dataInitializer,
                              SupplierRepository supplierRepository,
                              SupplierCategoryRepository categoryRepository) {
        this.dataInitializer = dataInitializer;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/sample-data")
    @Operation(summary = "Seed sample enterprise suppliers, evaluations, and categories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> triggerSeed() {
        dataInitializer.forceSeedSampleData();

        Map<String, Object> result = new HashMap<>();
        result.put("categoriesCount", categoryRepository.count());
        result.put("suppliersCount", supplierRepository.count());
        result.put("message", "Sample enterprise data seeded successfully");

        return ResponseEntity.ok(ApiResponse.success("Sample enterprise data initialized", result));
    }

    @GetMapping("/status")
    @Operation(summary = "Check current count of categories and suppliers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSeedStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("categoriesCount", categoryRepository.count());
        result.put("suppliersCount", supplierRepository.count());

        return ResponseEntity.ok(ApiResponse.success("Current database data status", result));
    }
}
