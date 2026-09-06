package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.EvaluationRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.EvaluationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.service.SupplierEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluations")
@Tag(name = "Supplier Evaluations", description = "Evaluate suppliers, compute weighted ratings, draft workflows, and manage scorecards")
@SecurityRequirement(name = "bearerAuth")
public class SupplierEvaluationController {

    private final SupplierEvaluationService evaluationService;

    public SupplierEvaluationController(SupplierEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping
    @Operation(summary = "Get evaluations with pagination and multi-attribute filters (Admin & Manager)")
    public ResponseEntity<ApiResponse<PaginatedResponse<EvaluationResponse>>> getEvaluations(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long evaluatorId,
            @RequestParam(required = false) EvaluationStatus status,
            @RequestParam(required = false) RatingCategory ratingCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "evaluationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PaginatedResponse<EvaluationResponse> response = evaluationService.getEvaluations(
                supplierId, evaluatorId, status, ratingCategory, startDate, endDate, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Evaluations fetched successfully", response));
    }

    @GetMapping("/my-evaluations")
    @Operation(summary = "Get evaluations created by the authenticated user (Manager & Admin)")
    public ResponseEntity<ApiResponse<PaginatedResponse<EvaluationResponse>>> getMyEvaluations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "evaluationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PaginatedResponse<EvaluationResponse> response = evaluationService.getMyEvaluations(page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("My evaluations fetched successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get evaluation by ID (Admin & Manager)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> getEvaluationById(@PathVariable Long id) {
        EvaluationResponse evaluation = evaluationService.getEvaluationById(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation fetched successfully", evaluation));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get evaluation by evaluation code (Admin & Manager)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> getEvaluationByCode(@PathVariable String code) {
        EvaluationResponse evaluation = evaluationService.getEvaluationByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Evaluation fetched successfully", evaluation));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get all evaluation records for a specific supplier (Admin & Manager)")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getEvaluationsBySupplier(@PathVariable Long supplierId) {
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsBySupplierId(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier evaluations fetched successfully", evaluations));
    }

    @PostMapping
    @Operation(summary = "Create and submit a supplier evaluation (Manager & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> createEvaluation(
            @Valid @RequestBody EvaluationRequest evaluationRequest) {
        EvaluationResponse response = evaluationService.createEvaluation(evaluationRequest);
        return new ResponseEntity<>(ApiResponse.success("Evaluation submitted successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/draft")
    @Operation(summary = "Save a supplier evaluation as draft (Manager & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> createDraftEvaluation(
            @Valid @RequestBody EvaluationRequest evaluationRequest) {
        EvaluationResponse response = evaluationService.createDraftEvaluation(evaluationRequest);
        return new ResponseEntity<>(ApiResponse.success("Evaluation saved as draft successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update draft evaluation details (Evaluator owner & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> updateDraftEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationRequest evaluationRequest) {
        EvaluationResponse response = evaluationService.updateDraftEvaluation(id, evaluationRequest);
        return ResponseEntity.ok(ApiResponse.success("Draft evaluation updated successfully", response));
    }

    @PatchMapping("/{id}/submit")
    @Operation(summary = "Submit a draft evaluation (Evaluator owner & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> submitEvaluation(@PathVariable Long id) {
        EvaluationResponse response = evaluationService.submitEvaluation(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation submitted successfully", response));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Finalize and complete an evaluation (Evaluator owner & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> completeEvaluation(@PathVariable Long id) {
        EvaluationResponse response = evaluationService.completeEvaluation(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation completed successfully", response));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an evaluation (Evaluator owner & Admin)")
    public ResponseEntity<ApiResponse<EvaluationResponse>> cancelEvaluation(@PathVariable Long id) {
        EvaluationResponse response = evaluationService.cancelEvaluation(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation cancelled successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete evaluation by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation deleted successfully"));
    }
}
