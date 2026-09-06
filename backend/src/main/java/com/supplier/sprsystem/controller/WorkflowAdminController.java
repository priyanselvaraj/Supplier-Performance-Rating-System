package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.WorkflowDefinitionRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.WorkflowDefinitionResponse;
import com.supplier.sprsystem.service.WorkflowDefinitionService;
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
@RequestMapping("/api/v1/admin/workflows")
@Tag(name = "Workflow Administration", description = "Admin configuration for workflow blueprints, steps, SLAs, and approval rules")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class WorkflowAdminController {

    private final WorkflowDefinitionService workflowDefinitionService;

    public WorkflowAdminController(WorkflowDefinitionService workflowDefinitionService) {
        this.workflowDefinitionService = workflowDefinitionService;
    }

    @GetMapping("/definitions")
    @Operation(summary = "Get all configured workflow blueprints")
    public ResponseEntity<ApiResponse<List<WorkflowDefinitionResponse>>> getAllDefinitions() {
        List<WorkflowDefinitionResponse> responses = workflowDefinitionService.getAllDefinitions();
        return ResponseEntity.ok(ApiResponse.success("Workflow definitions retrieved successfully", responses));
    }

    @GetMapping("/definitions/{id}")
    @Operation(summary = "Get a workflow blueprint by ID")
    public ResponseEntity<ApiResponse<WorkflowDefinitionResponse>> getDefinitionById(@PathVariable Long id) {
        WorkflowDefinitionResponse response = workflowDefinitionService.getDefinitionById(id);
        return ResponseEntity.ok(ApiResponse.success("Workflow definition retrieved successfully", response));
    }

    @PostMapping("/definitions")
    @Operation(summary = "Create a new workflow definition with sequential steps")
    public ResponseEntity<ApiResponse<WorkflowDefinitionResponse>> createDefinition(
            @Valid @RequestBody WorkflowDefinitionRequest request
    ) {
        WorkflowDefinitionResponse response = workflowDefinitionService.createDefinition(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workflow definition created successfully", response));
    }

    @PutMapping("/definitions/{id}")
    @Operation(summary = "Update an existing workflow definition and steps")
    public ResponseEntity<ApiResponse<WorkflowDefinitionResponse>> updateDefinition(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowDefinitionRequest request
    ) {
        WorkflowDefinitionResponse response = workflowDefinitionService.updateDefinition(id, request);
        return ResponseEntity.ok(ApiResponse.success("Workflow definition updated successfully", response));
    }

    @PatchMapping("/definitions/{id}/toggle")
    @Operation(summary = "Toggle active / inactive state of a workflow definition")
    public ResponseEntity<ApiResponse<WorkflowDefinitionResponse>> toggleDefinitionActive(@PathVariable Long id) {
        WorkflowDefinitionResponse response = workflowDefinitionService.toggleDefinitionActive(id);
        return ResponseEntity.ok(ApiResponse.success("Workflow definition status toggled successfully", response));
    }

    @DeleteMapping("/definitions/{id}")
    @Operation(summary = "Delete a workflow definition")
    public ResponseEntity<ApiResponse<Void>> deleteDefinition(@PathVariable Long id) {
        workflowDefinitionService.deleteDefinition(id);
        return ResponseEntity.ok(ApiResponse.success("Workflow definition deleted successfully", null));
    }
}
