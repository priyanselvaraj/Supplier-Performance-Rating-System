package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.KpiDefinitionRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.KpiDefinitionResponse;
import com.supplier.sprsystem.service.KpiService;
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
@RequestMapping("/api/v1/admin/kpis")
@Tag(name = "KPI Administration", description = "Admin endpoints for creating and managing KPI definition rules and thresholds")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class KpiAdminController {

    private final KpiService kpiService;

    public KpiAdminController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GetMapping
    @Operation(summary = "List all KPI definitions (active and inactive)")
    public ResponseEntity<ApiResponse<List<KpiDefinitionResponse>>> getAllKpis() {
        List<KpiDefinitionResponse> response = kpiService.getAllKpiDefinitions();
        return ResponseEntity.ok(ApiResponse.success("KPI definitions retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific KPI definition by ID")
    public ResponseEntity<ApiResponse<KpiDefinitionResponse>> getKpiById(@PathVariable Long id) {
        KpiDefinitionResponse response = kpiService.getKpiDefinitionById(id);
        return ResponseEntity.ok(ApiResponse.success("KPI definition retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new KPI definition with target values and thresholds")
    public ResponseEntity<ApiResponse<KpiDefinitionResponse>> createKpi(@Valid @RequestBody KpiDefinitionRequest request) {
        KpiDefinitionResponse response = kpiService.createKpiDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("KPI definition created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing KPI definition")
    public ResponseEntity<ApiResponse<KpiDefinitionResponse>> updateKpi(
            @PathVariable Long id,
            @Valid @RequestBody KpiDefinitionRequest request
    ) {
        KpiDefinitionResponse response = kpiService.updateKpiDefinition(id, request);
        return ResponseEntity.ok(ApiResponse.success("KPI definition updated successfully", response));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Activate or deactivate a KPI definition")
    public ResponseEntity<ApiResponse<KpiDefinitionResponse>> toggleKpi(@PathVariable Long id) {
        KpiDefinitionResponse response = kpiService.toggleKpiDefinition(id);
        return ResponseEntity.ok(ApiResponse.success("KPI definition status toggled successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a KPI definition")
    public ResponseEntity<ApiResponse<Void>> deleteKpi(@PathVariable Long id) {
        kpiService.deleteKpiDefinition(id);
        return ResponseEntity.ok(ApiResponse.success("KPI definition deleted successfully", null));
    }
}
