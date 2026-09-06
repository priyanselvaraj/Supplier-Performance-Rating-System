package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.DocumentReviewDto;
import com.supplier.sprsystem.dto.request.ProfileUpdateRequestReviewDto;
import com.supplier.sprsystem.dto.request.SupplierAccountCreateRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.SupplierDocumentResponse;
import com.supplier.sprsystem.dto.response.SupplierProfileUpdateRequestDto;
import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.service.SupplierPortalService;
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
@RequestMapping("/api/v1/supplier-portal/admin")
@Tag(name = "Supplier Portal Management", description = "Admin & Manager operations for provisioning supplier accounts, reviewing profile change requests, and auditing supplier documents")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class SupplierPortalAdminController {

    private final SupplierPortalService portalService;

    public SupplierPortalAdminController(SupplierPortalService portalService) {
        this.portalService = portalService;
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new portal login account for a specific supplier")
    public ResponseEntity<ApiResponse<UserResponse>> createSupplierUser(
            @Valid @RequestBody SupplierAccountCreateRequest request) {
        UserResponse response = portalService.createSupplierUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier portal account created successfully", response));
    }

    @GetMapping("/profile-requests")
    @Operation(summary = "Get all pending supplier profile update requests")
    public ResponseEntity<ApiResponse<List<SupplierProfileUpdateRequestDto>>> getPendingProfileRequests() {
        List<SupplierProfileUpdateRequestDto> response = portalService.getPendingProfileUpdateRequests();
        return ResponseEntity.ok(ApiResponse.success("Pending profile update requests retrieved", response));
    }

    @PutMapping("/profile-requests/{id}")
    @Operation(summary = "Approve or reject a supplier profile update request")
    public ResponseEntity<ApiResponse<SupplierProfileUpdateRequestDto>> reviewProfileRequest(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequestReviewDto reviewDto) {
        SupplierProfileUpdateRequestDto response = portalService.reviewProfileUpdateRequest(id, reviewDto);
        return ResponseEntity.ok(ApiResponse.success("Profile update request reviewed successfully", response));
    }

    @PutMapping("/documents/{id}/review")
    @Operation(summary = "Update audit status of a supplier document (Active, Rejected, Expired)")
    public ResponseEntity<ApiResponse<SupplierDocumentResponse>> reviewDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentReviewDto reviewDto) {
        SupplierDocumentResponse response = portalService.reviewDocument(id, reviewDto);
        return ResponseEntity.ok(ApiResponse.success("Document status updated successfully", response));
    }
}
