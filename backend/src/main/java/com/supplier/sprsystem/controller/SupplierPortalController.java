package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.SupplierActionResponseRequest;
import com.supplier.sprsystem.dto.request.SupplierCommunicationCreateRequest;
import com.supplier.sprsystem.dto.request.SupplierProfileUpdateSubmitRequest;
import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.service.SupplierPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier-portal")
@Tag(name = "Supplier Self-Service Portal", description = "Endpoints for authenticated supplier users to manage profiles, view ratings, inspect evaluation feedback, respond to CAP actions, manage documents, and communicate with procurement")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('SUPPLIER', 'ADMIN', 'MANAGER')")
public class SupplierPortalController {

    private final SupplierPortalService portalService;

    public SupplierPortalController(SupplierPortalService portalService) {
        this.portalService = portalService;
    }

    // ==========================================
    // 1. DASHBOARD & PROFILE
    // ==========================================

    @GetMapping("/dashboard")
    @Operation(summary = "Get supplier self-service dashboard summary and KPI metrics")
    public ResponseEntity<ApiResponse<SupplierPortalDashboardResponse>> getDashboard() {
        SupplierPortalDashboardResponse data = portalService.getSupplierDashboard();
        return ResponseEntity.ok(ApiResponse.success("Supplier dashboard loaded successfully", data));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current supplier profile details and update history")
    public ResponseEntity<ApiResponse<SupplierPortalProfileResponse>> getProfile() {
        SupplierPortalProfileResponse data = portalService.getSupplierProfile();
        return ResponseEntity.ok(ApiResponse.success("Supplier profile loaded successfully", data));
    }

    @PostMapping("/profile/request-update")
    @Operation(summary = "Submit a request to update supplier company or contact information")
    public ResponseEntity<ApiResponse<SupplierProfileUpdateRequestDto>> submitProfileUpdateRequest(
            @Valid @RequestBody SupplierProfileUpdateSubmitRequest request) {
        SupplierProfileUpdateRequestDto data = portalService.submitProfileUpdateRequest(request);
        return ResponseEntity.ok(ApiResponse.success("Profile update request submitted for manager review", data));
    }

    // ==========================================
    // 2. PERFORMANCE & EVALUATIONS
    // ==========================================

    @GetMapping("/performance")
    @Operation(summary = "Get comprehensive supplier performance scorecards, rating history, and AI insights")
    public ResponseEntity<ApiResponse<SupplierPortalPerformanceResponse>> getPerformance() {
        SupplierPortalPerformanceResponse data = portalService.getSupplierPerformance();
        return ResponseEntity.ok(ApiResponse.success("Supplier performance data loaded successfully", data));
    }

    @GetMapping("/evaluations")
    @Operation(summary = "Get supplier completed evaluation scorecards")
    public ResponseEntity<ApiResponse<List<SupplierPortalEvaluationResponse>>> getEvaluations() {
        List<SupplierPortalEvaluationResponse> data = portalService.getSupplierEvaluations();
        return ResponseEntity.ok(ApiResponse.success("Supplier evaluations retrieved successfully", data));
    }

    @GetMapping("/evaluations/{id}")
    @Operation(summary = "Get detailed evaluation scorecard with criteria scores and recommendations")
    public ResponseEntity<ApiResponse<SupplierPortalEvaluationResponse>> getEvaluationDetails(@PathVariable Long id) {
        SupplierPortalEvaluationResponse data = portalService.getSupplierEvaluationDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Evaluation scorecard loaded successfully", data));
    }

    // ==========================================
    // 3. IMPROVEMENT ACTIONS
    // ==========================================

    @GetMapping("/improvement-actions")
    @Operation(summary = "Get all corrective improvement actions assigned to this supplier")
    public ResponseEntity<ApiResponse<List<ImprovementActionResponse>>> getImprovementActions() {
        List<ImprovementActionResponse> data = portalService.getSupplierImprovementActions();
        return ResponseEntity.ok(ApiResponse.success("Improvement actions retrieved successfully", data));
    }

    @PostMapping("/improvement-actions/{id}/respond")
    @Operation(summary = "Submit progress update or response to an improvement action")
    public ResponseEntity<ApiResponse<ImprovementActionResponse>> respondToImprovementAction(
            @PathVariable Long id,
            @Valid @RequestBody SupplierActionResponseRequest request) {
        ImprovementActionResponse data = portalService.respondToImprovementAction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Improvement action response submitted successfully", data));
    }

    // ==========================================
    // 4. DOCUMENTS
    // ==========================================

    @GetMapping("/documents")
    @Operation(summary = "Get supplier uploaded certificates and compliance documents")
    public ResponseEntity<ApiResponse<List<SupplierDocumentResponse>>> getDocuments() {
        List<SupplierDocumentResponse> data = portalService.getSupplierDocuments();
        return ResponseEntity.ok(ApiResponse.success("Supplier documents retrieved successfully", data));
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new certificate, compliance, or registration document")
    public ResponseEntity<ApiResponse<SupplierDocumentResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", required = false, defaultValue = "General Document") String documentType,
            @RequestParam(value = "notes", required = false) String notes) {
        SupplierDocumentResponse data = portalService.uploadDocument(file, documentType, notes);
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", data));
    }

    @GetMapping("/documents/{id}/download")
    @Operation(summary = "Download a supplier document securely")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = portalService.downloadDocument(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Delete an uploaded document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        portalService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    // ==========================================
    // 5. COMMUNICATIONS
    // ==========================================

    @GetMapping("/communications")
    @Operation(summary = "Get supplier feedback and communication history")
    public ResponseEntity<ApiResponse<List<SupplierCommunicationResponse>>> getCommunications() {
        List<SupplierCommunicationResponse> data = portalService.getSupplierCommunications();
        return ResponseEntity.ok(ApiResponse.success("Communications retrieved successfully", data));
    }

    @PostMapping("/communications")
    @Operation(summary = "Send a new message or inquiry to the procurement team")
    public ResponseEntity<ApiResponse<SupplierCommunicationResponse>> sendCommunication(
            @Valid @RequestBody SupplierCommunicationCreateRequest request) {
        SupplierCommunicationResponse data = portalService.sendCommunication(request);
        return ResponseEntity.ok(ApiResponse.success("Message sent to procurement team", data));
    }

    // ==========================================
    // 6. NOTIFICATIONS
    // ==========================================

    @GetMapping("/notifications")
    @Operation(summary = "Get notifications for authenticated supplier user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications() {
        List<NotificationResponse> data = portalService.getSupplierNotifications();
        return ResponseEntity.ok(ApiResponse.success("Supplier notifications retrieved successfully", data));
    }
}
