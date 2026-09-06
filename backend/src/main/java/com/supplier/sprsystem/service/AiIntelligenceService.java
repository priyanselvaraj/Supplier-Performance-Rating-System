package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.*;

import java.util.List;

public interface AiIntelligenceService {

    SupplierPredictionResponse getSupplierPrediction(Long supplierId);

    SupplierRiskResponse getSupplierRisk(Long supplierId);

    SupplierAiTrendResponse getSupplierTrend(Long supplierId);

    List<AiRecommendationResponse> getSupplierRecommendations(Long supplierId);

    List<AiAlertResponse> getSupplierAlerts(Long supplierId);

    List<AiAlertResponse> getAllActiveAlerts();

    SupplierAiInsightsResponse getSupplierInsights(Long supplierId);

    AiDashboardResponse getAiDashboardSummary();
}
