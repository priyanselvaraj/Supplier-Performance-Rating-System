package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.dto.response.WorkflowInstanceResponse;
import com.supplier.sprsystem.dto.response.WorkflowSummaryResponse;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.model.entity.WorkflowStatus;
import com.supplier.sprsystem.model.entity.WorkflowType;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.WorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@Tag(name = "Workflow & Approval Engine", description = "Multi-level workflows, task routing, SLA management, and approval audit histories")
@SecurityRequirement(name = "bearerAuth")
public class WorkflowController {

    private final WorkflowEngineService workflowEngineService;
    private final UserRepository userRepository;

    public WorkflowController(WorkflowEngineService workflowEngineService, UserRepository userRepository) {
        this.workflowEngineService = workflowEngineService;
        this.userRepository = userRepository;
    }

    private User resolveCurrentUser(UserDetails userDetails) {
        if (userDetails != null) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
        }
        return userRepository.findAll().stream().findFirst().orElse(null);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search, filter, and paginate workflow instances")
    public ResponseEntity<ApiResponse<PaginatedResponse<WorkflowInstanceResponse>>> getWorkflows(
            @RequestParam(required = false) WorkflowType type,
            @RequestParam(required = false) WorkflowStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        PaginatedResponse<WorkflowInstanceResponse> response = workflowEngineService.searchAndFilterWorkflows(
                type, status, start, end, keyword, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Workflows retrieved successfully", response));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get workflow and approval summary metrics")
    public ResponseEntity<ApiResponse<WorkflowSummaryResponse>> getWorkflowSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        WorkflowSummaryResponse summary = workflowEngineService.getWorkflowSummary(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Workflow summary retrieved successfully", summary));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    @Operation(summary = "Get workflow instance details with visual timeline and audit trail")
    public ResponseEntity<ApiResponse<WorkflowInstanceResponse>> getWorkflowById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        WorkflowInstanceResponse response = workflowEngineService.getWorkflowInstanceById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Workflow details retrieved successfully", response));
    }

    @GetMapping("/resource/{resourceType}/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    @Operation(summary = "Get workflows associated with a specific business resource")
    public ResponseEntity<ApiResponse<List<WorkflowInstanceResponse>>> getWorkflowsByResource(
            @PathVariable String resourceType,
            @PathVariable Long resourceId
    ) {
        List<WorkflowInstanceResponse> responses = workflowEngineService.getWorkflowsByResource(resourceType, resourceId);
        return ResponseEntity.ok(ApiResponse.success("Resource workflows retrieved successfully", responses));
    }
}
