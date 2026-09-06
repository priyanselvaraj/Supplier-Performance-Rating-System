package com.supplier.sprsystem.dto.response;

import java.util.List;

public class SupplierAiInsightsResponse {
    private SupplierPredictionResponse prediction;
    private SupplierRiskResponse risk;
    private SupplierAiTrendResponse trend;
    private List<AiRecommendationResponse> recommendations;
    private List<AiAlertResponse> alerts;

    public SupplierAiInsightsResponse() {}

    public SupplierAiInsightsResponse(SupplierPredictionResponse prediction, SupplierRiskResponse risk, SupplierAiTrendResponse trend, List<AiRecommendationResponse> recommendations, List<AiAlertResponse> alerts) {
        this.prediction = prediction;
        this.risk = risk;
        this.trend = trend;
        this.recommendations = recommendations;
        this.alerts = alerts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SupplierPredictionResponse prediction;
        private SupplierRiskResponse risk;
        private SupplierAiTrendResponse trend;
        private List<AiRecommendationResponse> recommendations;
        private List<AiAlertResponse> alerts;

        public Builder prediction(SupplierPredictionResponse pred) { this.prediction = pred; return this; }
        public Builder risk(SupplierRiskResponse risk) { this.risk = risk; return this; }
        public Builder trend(SupplierAiTrendResponse trend) { this.trend = trend; return this; }
        public Builder recommendations(List<AiRecommendationResponse> recs) { this.recommendations = recs; return this; }
        public Builder alerts(List<AiAlertResponse> alerts) { this.alerts = alerts; return this; }

        public SupplierAiInsightsResponse build() {
            return new SupplierAiInsightsResponse(prediction, risk, trend, recommendations, alerts);
        }
    }

    public SupplierPredictionResponse getPrediction() { return prediction; }
    public void setPrediction(SupplierPredictionResponse prediction) { this.prediction = prediction; }
    public SupplierRiskResponse getRisk() { return risk; }
    public void setRisk(SupplierRiskResponse risk) { this.risk = risk; }
    public SupplierAiTrendResponse getTrend() { return trend; }
    public void setTrend(SupplierAiTrendResponse trend) { this.trend = trend; }
    public List<AiRecommendationResponse> getRecommendations() { return recommendations; }
    public void setRecommendations(List<AiRecommendationResponse> recommendations) { this.recommendations = recommendations; }
    public List<AiAlertResponse> getAlerts() { return alerts; }
    public void setAlerts(List<AiAlertResponse> alerts) { this.alerts = alerts; }
}
