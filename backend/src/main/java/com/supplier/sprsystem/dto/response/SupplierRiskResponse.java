package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.RiskLevel;

import java.util.List;

public class SupplierRiskResponse {
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private Double currentScore;
    private RatingCategory ratingCategory;
    private PerformanceTrend trend;
    private Double riskScore;
    private RiskLevel riskLevel;
    private List<String> riskFactors;
    private String explanation;
    private int activeAlertsCount;

    public SupplierRiskResponse() {}

    public SupplierRiskResponse(Long supplierId, String supplierName, String supplierCode, Double currentScore, RatingCategory ratingCategory, PerformanceTrend trend, Double riskScore, RiskLevel riskLevel, List<String> riskFactors, String explanation, int activeAlertsCount) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierCode = supplierCode;
        this.currentScore = currentScore;
        this.ratingCategory = ratingCategory;
        this.trend = trend;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.explanation = explanation;
        this.activeAlertsCount = activeAlertsCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierName;
        private String supplierCode;
        private Double currentScore;
        private RatingCategory ratingCategory;
        private PerformanceTrend trend;
        private Double riskScore;
        private RiskLevel riskLevel;
        private List<String> riskFactors;
        private String explanation;
        private int activeAlertsCount;

        public Builder supplierId(Long id) { this.supplierId = id; return this; }
        public Builder supplierName(String name) { this.supplierName = name; return this; }
        public Builder supplierCode(String code) { this.supplierCode = code; return this; }
        public Builder currentScore(Double score) { this.currentScore = score; return this; }
        public Builder ratingCategory(RatingCategory cat) { this.ratingCategory = cat; return this; }
        public Builder trend(PerformanceTrend trend) { this.trend = trend; return this; }
        public Builder riskScore(Double riskScore) { this.riskScore = riskScore; return this; }
        public Builder riskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; return this; }
        public Builder riskFactors(List<String> factors) { this.riskFactors = factors; return this; }
        public Builder explanation(String exp) { this.explanation = exp; return this; }
        public Builder activeAlertsCount(int count) { this.activeAlertsCount = count; return this; }

        public SupplierRiskResponse build() {
            return new SupplierRiskResponse(supplierId, supplierName, supplierCode, currentScore, ratingCategory, trend, riskScore, riskLevel, riskFactors, explanation, activeAlertsCount);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public PerformanceTrend getTrend() { return trend; }
    public void setTrend(PerformanceTrend trend) { this.trend = trend; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public int getActiveAlertsCount() { return activeAlertsCount; }
    public void setActiveAlertsCount(int activeAlertsCount) { this.activeAlertsCount = activeAlertsCount; }
}
