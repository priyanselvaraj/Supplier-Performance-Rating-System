package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.ApprovalActionRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.ApprovalTaskResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.TaskStatus;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.WorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/approvals")
@Tag(name = "Approval Management", description = "Endpoints for managing pending tasks, decisions, and history")
@SecurityRequirement(name = "bearerAuth")
public class ApprovalController {

    private final WorkflowEngineService workflowEngineService;
    private final UserRepository userRepository;

    public ApprovalController(WorkflowEngineService workflowEngineService, UserRepository userRepository) {
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

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get pending approval tasks assigned to the current user's role")
    public ResponseEntity<ApiResponse<PaginatedResponse<ApprovalTaskResponse>>> getMyPendingApprovals(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        PaginatedResponse<ApprovalTaskResponse> response = workflowEngineService.getMyPendingTasks(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("Pending approvals retrieved successfully", response));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get processed approval tasks history actioned by the current user")
    public ResponseEntity<ApiResponse<PaginatedResponse<ApprovalTaskResponse>>> getMyApprovalHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        PaginatedResponse<ApprovalTaskResponse> response = workflowEngineService.getMyApprovalHistory(currentUser, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Approval history retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get approval task details by task ID")
    public ResponseEntity<ApiResponse<ApprovalTaskResponse>> getApprovalTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        ApprovalTaskResponse response = workflowEngineService.getApprovalTaskById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Approval task retrieved successfully", response));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve a pending task and transition workflow to the next step or completion")
    public ResponseEntity<ApiResponse<ApprovalTaskResponse>> approveTask(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ApprovalActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);

        if (request == null) {
            request = new ApprovalActionRequest();
        }

        ApprovalTaskResponse response = workflowEngineService.approveTask(id, currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Task approved successfully", response));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Reject a pending task with a required reason")
    public ResponseEntity<ApiResponse<ApprovalTaskResponse>> rejectTask(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        ApprovalTaskResponse response = workflowEngineService.rejectTask(id, currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Task rejected successfully", response));
    }
}
