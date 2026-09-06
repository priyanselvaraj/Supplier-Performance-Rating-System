package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.ManualEscalationRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.WorkflowEscalationResponse;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/escalations")
@Tag(name = "Workflow Escalation Engine", description = "SLA tracking, overdue detection, and escalation registry")
@SecurityRequirement(name = "bearerAuth")
public class WorkflowEscalationController {

    private final WorkflowEngineService workflowEngineService;
    private final UserRepository userRepository;

    public WorkflowEscalationController(WorkflowEngineService workflowEngineService, UserRepository userRepository) {
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
    @Operation(summary = "Get list of SLA breaches and manual escalations")
    public ResponseEntity<ApiResponse<List<WorkflowEscalationResponse>>> getEscalations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<WorkflowEscalationResponse> responses = workflowEngineService.getEscalations(page, size);
        return ResponseEntity.ok(ApiResponse.success("Escalations retrieved successfully", responses));
    }

    @PostMapping("/check-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trigger automatic SLA scan and escalate overdue pending tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkOverdueTasks() {
        int escalatedCount = workflowEngineService.checkAndEscalateOverdueTasks();
        return ResponseEntity.ok(ApiResponse.success(
                "Overdue task scan completed",
                Map.of("escalatedCount", escalatedCount)
        ));
    }

    @PostMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Manually escalate a pending approval task")
    public ResponseEntity<ApiResponse<WorkflowEscalationResponse>> escalateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody ManualEscalationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = resolveCurrentUser(userDetails);
        WorkflowEscalationResponse response = workflowEngineService.escalateTask(taskId, currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Task escalated successfully", response));
    }
}
