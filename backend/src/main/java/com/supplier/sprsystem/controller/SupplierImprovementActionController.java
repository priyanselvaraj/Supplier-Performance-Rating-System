package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.ImprovementActionRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.ImprovementActionResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.SupplierImprovementActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/improvement-actions")
@Tag(name = "Supplier Improvement Actions", description = "Collaborative supplier remediation action planning and status tracking")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class SupplierImprovementActionController {

    private final SupplierImprovementActionService actionService;
    private final UserRepository userRepository;

    public SupplierImprovementActionController(SupplierImprovementActionService actionService, UserRepository userRepository) {
        this.actionService = actionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get paginated supplier improvement actions with filtering")
    public ResponseEntity<ApiResponse<PaginatedResponse<ImprovementActionResponse>>> getActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) ImprovementActionStatus status,
            @RequestParam(required = false) ImprovementActionPriority priority,
            @RequestParam(required = false) Long assignedUserId
    ) {
        PaginatedResponse<ImprovementActionResponse> response = actionService.getActions(page, size, supplierId, status, priority, assignedUserId);
        return ResponseEntity.ok(ApiResponse.success("Improvement actions retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get improvement action details by ID")
    public ResponseEntity<ApiResponse<ImprovementActionResponse>> getActionById(@PathVariable Long id) {
        ImprovementActionResponse response = actionService.getActionById(id);
        return ResponseEntity.ok(ApiResponse.success("Improvement action retrieved successfully", response));
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get all improvement actions for a specific supplier")
    public ResponseEntity<ApiResponse<List<ImprovementActionResponse>>> getActionsBySupplierId(@PathVariable Long supplierId) {
        List<ImprovementActionResponse> response = actionService.getActionsBySupplierId(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier improvement actions retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Create a new supplier improvement action")
    public ResponseEntity<ApiResponse<ImprovementActionResponse>> createAction(
            @Valid @RequestBody ImprovementActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long currentUserId = getUserId(userDetails);
        ImprovementActionResponse response = actionService.createAction(request, currentUserId);
        return new ResponseEntity<>(ApiResponse.success("Improvement action created successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing improvement action")
    public ResponseEntity<ApiResponse<ImprovementActionResponse>> updateAction(
            @PathVariable Long id,
            @Valid @RequestBody ImprovementActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long currentUserId = getUserId(userDetails);
        ImprovementActionResponse response = actionService.updateAction(id, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Improvement action updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update improvement action status and resolution notes")
    public ResponseEntity<ApiResponse<ImprovementActionResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long currentUserId = getUserId(userDetails);
        ImprovementActionStatus status = ImprovementActionStatus.valueOf(payload.get("status"));
        String notes = payload.get("resolutionNotes");
        ImprovementActionResponse response = actionService.updateActionStatus(id, status, notes, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Improvement action status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an improvement action (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteAction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long currentUserId = getUserId(userDetails);
        actionService.deleteAction(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Improvement action deleted successfully", null));
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) return 1L;
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        return user != null ? user.getId() : 1L;
    }
}
