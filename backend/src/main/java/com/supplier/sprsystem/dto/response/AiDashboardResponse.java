package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.RiskLevel;

import java.util.List;
import java.util.Map;

public class AiDashboardResponse {
    private int totalSuppliersAnalyzed;
    private int lowRiskCount;
    private int mediumRiskCount;
    private int highRiskCount;
    private int criticalRiskCount;
    private int decliningSuppliersCount;
    private int activeAlertsCount;
    private Double averageSystemRiskScore;
    private Map<RiskLevel, Long> riskDistribution;
    private List<AiAlertResponse> criticalAlerts;
    private List<SupplierRiskResponse> topRiskSuppliers;
    private List<SupplierPredictionResponse> decliningPredictions;
    private List<AiRecommendationResponse> topRecommendations;

    public AiDashboardResponse() {}

    public AiDashboardResponse(int totalSuppliersAnalyzed, int lowRiskCount, int mediumRiskCount, int highRiskCount, int criticalRiskCount, int decliningSuppliersCount, int activeAlertsCount, Double averageSystemRiskScore, Map<RiskLevel, Long> riskDistribution, List<AiAlertResponse> criticalAlerts, List<SupplierRiskResponse> topRiskSuppliers, List<SupplierPredictionResponse> decliningPredictions, List<AiRecommendationResponse> topRecommendations) {
        this.totalSuppliersAnalyzed = totalSuppliersAnalyzed;
        this.lowRiskCount = lowRiskCount;
        this.mediumRiskCount = mediumRiskCount;
        this.highRiskCount = highRiskCount;
        this.criticalRiskCount = criticalRiskCount;
        this.decliningSuppliersCount = decliningSuppliersCount;
        this.activeAlertsCount = activeAlertsCount;
        this.averageSystemRiskScore = averageSystemRiskScore;
        this.riskDistribution = riskDistribution;
        this.criticalAlerts = criticalAlerts;
        this.topRiskSuppliers = topRiskSuppliers;
        this.decliningPredictions = decliningPredictions;
        this.topRecommendations = topRecommendations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalSuppliersAnalyzed;
        private int lowRiskCount;
        private int mediumRiskCount;
        private int highRiskCount;
        private int criticalRiskCount;
        private int decliningSuppliersCount;
        private int activeAlertsCount;
        private Double averageSystemRiskScore;
        private Map<RiskLevel, Long> riskDistribution;
        private List<AiAlertResponse> criticalAlerts;
        private List<SupplierRiskResponse> topRiskSuppliers;
        private List<SupplierPredictionResponse> decliningPredictions;
        private List<AiRecommendationResponse> topRecommendations;

        public Builder totalSuppliersAnalyzed(int total) { this.totalSuppliersAnalyzed = total; return this; }
        public Builder lowRiskCount(int count) { this.lowRiskCount = count; return this; }
        public Builder mediumRiskCount(int count) { this.mediumRiskCount = count; return this; }
        public Builder highRiskCount(int count) { this.highRiskCount = count; return this; }
        public Builder criticalRiskCount(int count) { this.criticalRiskCount = count; return this; }
        public Builder decliningSuppliersCount(int count) { this.decliningSuppliersCount = count; return this; }
        public Builder activeAlertsCount(int count) { this.activeAlertsCount = count; return this; }
        public Builder averageSystemRiskScore(Double avg) { this.averageSystemRiskScore = avg; return this; }
        public Builder riskDistribution(Map<RiskLevel, Long> dist) { this.riskDistribution = dist; return this; }
        public Builder criticalAlerts(List<AiAlertResponse> alerts) { this.criticalAlerts = alerts; return this; }
        public Builder topRiskSuppliers(List<SupplierRiskResponse> list) { this.topRiskSuppliers = list; return this; }
        public Builder decliningPredictions(List<SupplierPredictionResponse> list) { this.decliningPredictions = list; return this; }
        public Builder topRecommendations(List<AiRecommendationResponse> list) { this.topRecommendations = list; return this; }

        public AiDashboardResponse build() {
            return new AiDashboardResponse(totalSuppliersAnalyzed, lowRiskCount, mediumRiskCount, highRiskCount, criticalRiskCount, decliningSuppliersCount, activeAlertsCount, averageSystemRiskScore, riskDistribution, criticalAlerts, topRiskSuppliers, decliningPredictions, topRecommendations);
        }
    }

    public int getTotalSuppliersAnalyzed() { return totalSuppliersAnalyzed; }
    public void setTotalSuppliersAnalyzed(int totalSuppliersAnalyzed) { this.totalSuppliersAnalyzed = totalSuppliersAnalyzed; }
    public int getLowRiskCount() { return lowRiskCount; }
    public void setLowRiskCount(int lowRiskCount) { this.lowRiskCount = lowRiskCount; }
    public int getMediumRiskCount() { return mediumRiskCount; }
    public void setMediumRiskCount(int mediumRiskCount) { this.mediumRiskCount = mediumRiskCount; }
    public int getHighRiskCount() { return highRiskCount; }
    public void setHighRiskCount(int highRiskCount) { this.highRiskCount = highRiskCount; }
    public int getCriticalRiskCount() { return criticalRiskCount; }
    public void setCriticalRiskCount(int criticalRiskCount) { this.criticalRiskCount = criticalRiskCount; }
    public int getDecliningSuppliersCount() { return decliningSuppliersCount; }
    public void setDecliningSuppliersCount(int decliningSuppliersCount) { this.decliningSuppliersCount = decliningSuppliersCount; }
    public int getActiveAlertsCount() { return activeAlertsCount; }
    public void setActiveAlertsCount(int activeAlertsCount) { this.activeAlertsCount = activeAlertsCount; }
    public Double getAverageSystemRiskScore() { return averageSystemRiskScore; }
    public void setAverageSystemRiskScore(Double averageSystemRiskScore) { this.averageSystemRiskScore = averageSystemRiskScore; }
    public Map<RiskLevel, Long> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(Map<RiskLevel, Long> riskDistribution) { this.riskDistribution = riskDistribution; }
    public List<AiAlertResponse> getCriticalAlerts() { return criticalAlerts; }
    public void setCriticalAlerts(List<AiAlertResponse> criticalAlerts) { this.criticalAlerts = criticalAlerts; }
    public List<SupplierRiskResponse> getTopRiskSuppliers() { return topRiskSuppliers; }
    public void setTopRiskSuppliers(List<SupplierRiskResponse> topRiskSuppliers) { this.topRiskSuppliers = topRiskSuppliers; }
    public List<SupplierPredictionResponse> getDecliningPredictions() { return decliningPredictions; }
    public void setDecliningPredictions(List<SupplierPredictionResponse> decliningPredictions) { this.decliningPredictions = decliningPredictions; }
    public List<AiRecommendationResponse> getTopRecommendations() { return topRecommendations; }
    public void setTopRecommendations(List<AiRecommendationResponse> topRecommendations) { this.topRecommendations = topRecommendations; }
}
