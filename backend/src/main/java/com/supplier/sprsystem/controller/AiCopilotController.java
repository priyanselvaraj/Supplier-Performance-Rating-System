package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.ai.*;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.model.entity.AiRecommendationDecision;
import com.supplier.sprsystem.service.AiCopilotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Copilot & Intelligent Decision Support", description = "Conversational natural language analytics, automated risk explanations, supplier comparison, and human-in-the-loop recommendations")
public class AiCopilotController {

    private final AiCopilotService copilotService;

    public AiCopilotController(AiCopilotService copilotService) {
        this.copilotService = copilotService;
    }

    @PostMapping("/copilot/query")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    @Operation(summary = "Process natural language analytical questions with role-based data isolation")
    public ResponseEntity<ApiResponse<AiCopilotQueryResponse>> processCopilotQuery(
            @Valid @RequestBody AiCopilotQueryRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        AiCopilotQueryResponse response = copilotService.processCopilotQuery(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("AI Query processed successfully", response));
    }

    @GetMapping("/copilot/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    @Operation(summary = "Get user AI query interaction history")
    public ResponseEntity<ApiResponse<List<AiCopilotHistoryResponse>>> getInteractionHistory(
            @AuthenticationPrincipal UserDetails currentUser) {
        List<AiCopilotHistoryResponse> history = copilotService.getInteractionHistory(currentUser);
        return ResponseEntity.ok(ApiResponse.success("AI Interaction history retrieved successfully", history));
    }

    @PostMapping("/copilot/feedback/{historyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPPLIER')")
    @Operation(summary = "Submit quality feedback on AI Copilot response")
    public ResponseEntity<ApiResponse<String>> submitFeedback(
            @PathVariable Long historyId,
            @Valid @RequestBody AiFeedbackRequest feedback,
            @AuthenticationPrincipal UserDetails currentUser) {
        copilotService.submitFeedback(historyId, feedback, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Feedback recorded successfully", "Feedback logged"));
    }

    @PostMapping("/suppliers/compare")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate comparative AI analysis across multiple suppliers")
    public ResponseEntity<ApiResponse<AiSupplierCompareResponse>> compareSuppliers(
            @Valid @RequestBody AiSupplierCompareRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        AiSupplierCompareResponse comparison = copilotService.compareSuppliers(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Supplier AI comparison completed successfully", comparison));
    }

    @GetMapping("/executive-insights")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get comprehensive C-Suite AI decision support summary")
    public ResponseEntity<ApiResponse<ExecutiveAiSummaryResponse>> getExecutiveAiInsights(
            @AuthenticationPrincipal UserDetails currentUser) {
        ExecutiveAiSummaryResponse insights = copilotService.getExecutiveAiInsights(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Executive AI insights generated successfully", insights));
    }

    @GetMapping("/workflows/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get AI recommendations for workflow escalations and bottleneck resolutions")
    public ResponseEntity<ApiResponse<List<AiWorkflowRecommendationResponse>>> getWorkflowRecommendations(
            @AuthenticationPrincipal UserDetails currentUser) {
        List<AiWorkflowRecommendationResponse> recommendations = copilotService.getWorkflowRecommendations(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Workflow recommendations retrieved successfully", recommendations));
    }

    @PostMapping("/recommendations/{recommendationId}/decision")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Record human decision (Accept/Dismiss/Create Action) on AI recommendation")
    public ResponseEntity<ApiResponse<AiRecommendationDecision>> handleRecommendationDecision(
            @PathVariable Long recommendationId,
            @Valid @RequestBody RecommendationDecisionRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        AiRecommendationDecision decision = copilotService.handleRecommendationDecision(recommendationId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Recommendation decision recorded successfully", decision));
    }

    @GetMapping("/suppliers/{supplierId}/decisions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get human recommendation decisions for a supplier")
    public ResponseEntity<ApiResponse<List<AiRecommendationDecision>>> getRecommendationDecisions(
            @PathVariable Long supplierId,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<AiRecommendationDecision> decisions = copilotService.getRecommendationDecisions(supplierId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Supplier recommendation decisions retrieved successfully", decisions));
    }
}
