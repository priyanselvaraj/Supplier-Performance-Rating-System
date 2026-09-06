package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.response.*;
import com.supplier.sprsystem.service.AiIntelligenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Intelligence & Predictive Analytics", description = "AI-powered supplier risk scoring, performance prediction, and early warning analytics")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AiIntelligenceController {

    private final AiIntelligenceService aiService;

    public AiIntelligenceController(AiIntelligenceService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get executive AI analytics dashboard summary with risk distribution and alerts")
    public ResponseEntity<ApiResponse<AiDashboardResponse>> getAiDashboardSummary() {
        AiDashboardResponse dashboard = aiService.getAiDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("AI Dashboard summary retrieved successfully", dashboard));
    }

    @GetMapping("/suppliers/{supplierId}/prediction")
    @Operation(summary = "Get next-cycle performance score prediction and explainable trajectory")
    public ResponseEntity<ApiResponse<SupplierPredictionResponse>> getSupplierPrediction(@PathVariable Long supplierId) {
        SupplierPredictionResponse prediction = aiService.getSupplierPrediction(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier performance prediction generated successfully", prediction));
    }

    @GetMapping("/suppliers/{supplierId}/risk")
    @Operation(summary = "Get supplier composite risk score, risk level, and explainable risk factors")
    public ResponseEntity<ApiResponse<SupplierRiskResponse>> getSupplierRisk(@PathVariable Long supplierId) {
        SupplierRiskResponse risk = aiService.getSupplierRisk(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier risk analysis retrieved successfully", risk));
    }

    @GetMapping("/suppliers/{supplierId}/trend")
    @Operation(summary = "Get historical trajectory and velocity rate for supplier")
    public ResponseEntity<ApiResponse<SupplierAiTrendResponse>> getSupplierTrend(@PathVariable Long supplierId) {
        SupplierAiTrendResponse trend = aiService.getSupplierTrend(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier trend analysis retrieved successfully", trend));
    }

    @GetMapping("/suppliers/{supplierId}/recommendations")
    @Operation(summary = "Get prioritized criteria-specific remediation recommendations")
    public ResponseEntity<ApiResponse<List<AiRecommendationResponse>>> getSupplierRecommendations(@PathVariable Long supplierId) {
        List<AiRecommendationResponse> recommendations = aiService.getSupplierRecommendations(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier recommendations generated successfully", recommendations));
    }

    @GetMapping("/suppliers/{supplierId}/alerts")
    @Operation(summary = "Get early warning triggers and active alerts for supplier")
    public ResponseEntity<ApiResponse<List<AiAlertResponse>>> getSupplierAlerts(@PathVariable Long supplierId) {
        List<AiAlertResponse> alerts = aiService.getSupplierAlerts(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier active alerts retrieved successfully", alerts));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get all system-wide active AI early warning alerts")
    public ResponseEntity<ApiResponse<List<AiAlertResponse>>> getAllAlerts() {
        List<AiAlertResponse> alerts = aiService.getAllActiveAlerts();
        return ResponseEntity.ok(ApiResponse.success("All active alerts retrieved successfully", alerts));
    }

    @GetMapping("/suppliers/{supplierId}/insights")
    @Operation(summary = "Get unified AI supplier intelligence package (Prediction + Risk + Trend + Recommendations + Alerts)")
    public ResponseEntity<ApiResponse<SupplierAiInsightsResponse>> getSupplierInsights(@PathVariable Long supplierId) {
        SupplierAiInsightsResponse insights = aiService.getSupplierInsights(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier AI intelligence insights retrieved successfully", insights));
    }
}
