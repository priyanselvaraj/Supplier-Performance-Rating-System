package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.EvaluationCriteriaRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.EvaluationCriteriaResponse;
import com.supplier.sprsystem.service.EvaluationCriteriaService;
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
@RequestMapping({"/api/v1/evaluation-criteria", "/api/v1/criteria"})
@Tag(name = "Evaluation Criteria", description = "Performance evaluation criteria management and weights configuration")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService criteriaService;

    public EvaluationCriteriaController(EvaluationCriteriaService criteriaService) {
        this.criteriaService = criteriaService;
    }

    @GetMapping
    @Operation(summary = "Get all evaluation criteria (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getAllCriteria() {
        List<EvaluationCriteriaResponse> criteriaList = criteriaService.getAllCriteria();
        return ResponseEntity.ok(ApiResponse.success("Evaluation criteria fetched successfully", criteriaList));
    }

    @GetMapping("/active")
    @Operation(summary = "Get only active evaluation criteria for scoring form (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getActiveCriteria() {
        List<EvaluationCriteriaResponse> criteriaList = criteriaService.getActiveCriteria();
        return ResponseEntity.ok(ApiResponse.success("Active criteria fetched successfully", criteriaList));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get criteria by ID (Admin & Manager)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getCriteriaById(@PathVariable Long id) {
        EvaluationCriteriaResponse criteria = criteriaService.getCriteriaById(id);
        return ResponseEntity.ok(ApiResponse.success("Criteria fetched successfully", criteria));
    }

    @GetMapping("/total-weight")
    @Operation(summary = "Get sum of active criteria weights (Admin & Manager)")
    public ResponseEntity<ApiResponse<Double>> getTotalActiveWeights() {
        Double totalWeight = criteriaService.getTotalActiveWeights();
        return ResponseEntity.ok(ApiResponse.success("Total active weights fetched", totalWeight));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new evaluation criteria (Admin only)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> createCriteria(
            @Valid @RequestBody EvaluationCriteriaRequest criteriaRequest) {
        EvaluationCriteriaResponse created = criteriaService.createCriteria(criteriaRequest);
        return new ResponseEntity<>(ApiResponse.success("Criteria created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update evaluation criteria (Admin only)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> updateCriteria(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCriteriaRequest criteriaRequest) {
        EvaluationCriteriaResponse updated = criteriaService.updateCriteria(id, criteriaRequest);
        return ResponseEntity.ok(ApiResponse.success("Criteria updated successfully", updated));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate criteria (Admin only)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> activateCriteria(@PathVariable Long id) {
        EvaluationCriteriaResponse updated = criteriaService.activateCriteria(id);
        return ResponseEntity.ok(ApiResponse.success("Criteria activated successfully", updated));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate criteria (Admin only)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> deactivateCriteria(@PathVariable Long id) {
        EvaluationCriteriaResponse updated = criteriaService.deactivateCriteria(id);
        return ResponseEntity.ok(ApiResponse.success("Criteria deactivated successfully", updated));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle criteria active status (Admin only)")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> toggleCriteriaActive(@PathVariable Long id) {
        EvaluationCriteriaResponse updated = criteriaService.toggleCriteriaActive(id);
        return ResponseEntity.ok(ApiResponse.success("Criteria active status toggled", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete criteria safely (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteCriteria(@PathVariable Long id) {
        criteriaService.deleteCriteria(id);
        return ResponseEntity.ok(ApiResponse.success("Criteria deleted successfully"));
    }
}
