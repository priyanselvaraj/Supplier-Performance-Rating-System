package com.supplier.sprsystem.dto.ai;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AiSupplierCompareResponse {

    private LocalDate comparisonDate;
    private List<SupplierComparisonSummary> suppliers;
    private String comparativeAnalysis;
    private Long recommendedSupplierId;
    private String recommendedSupplierName;
    private String recommendationRationale;
    private Map<String, List<String>> strengthsBySupplier;
    private Map<String, List<String>> weaknessesBySupplier;

    public AiSupplierCompareResponse() {}

    public AiSupplierCompareResponse(LocalDate comparisonDate, List<SupplierComparisonSummary> suppliers, String comparativeAnalysis, Long recommendedSupplierId, String recommendedSupplierName, String recommendationRationale, Map<String, List<String>> strengthsBySupplier, Map<String, List<String>> weaknessesBySupplier) {
        this.comparisonDate = comparisonDate;
        this.suppliers = suppliers;
        this.comparativeAnalysis = comparativeAnalysis;
        this.recommendedSupplierId = recommendedSupplierId;
        this.recommendedSupplierName = recommendedSupplierName;
        this.recommendationRationale = recommendationRationale;
        this.strengthsBySupplier = strengthsBySupplier;
        this.weaknessesBySupplier = weaknessesBySupplier;
    }

    public static class SupplierComparisonSummary {
        private Long supplierId;
        private String supplierName;
        private String supplierCode;
        private String categoryName;
        private Double overallRating;
        private String ratingCategory;
        private String riskLevel;
        private Double riskScore;
        private String trend;
        private int evaluationsCount;

        public SupplierComparisonSummary() {}

        public SupplierComparisonSummary(Long supplierId, String supplierName, String supplierCode, String categoryName, Double overallRating, String ratingCategory, String riskLevel, Double riskScore, String trend, int evaluationsCount) {
            this.supplierId = supplierId;
            this.supplierName = supplierName;
            this.supplierCode = supplierCode;
            this.categoryName = categoryName;
            this.overallRating = overallRating;
            this.ratingCategory = ratingCategory;
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.trend = trend;
            this.evaluationsCount = evaluationsCount;
        }

        public Long getSupplierId() { return supplierId; }
        public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
        public String getSupplierName() { return supplierName; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
        public String getSupplierCode() { return supplierCode; }
        public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public Double getOverallRating() { return overallRating; }
        public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
        public String getRatingCategory() { return ratingCategory; }
        public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public Double getRiskScore() { return riskScore; }
        public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
        public int getEvaluationsCount() { return evaluationsCount; }
        public void setEvaluationsCount(int evaluationsCount) { this.evaluationsCount = evaluationsCount; }
    }

    public LocalDate getComparisonDate() { return comparisonDate; }
    public void setComparisonDate(LocalDate comparisonDate) { this.comparisonDate = comparisonDate; }
    public List<SupplierComparisonSummary> getSuppliers() { return suppliers; }
    public void setSuppliers(List<SupplierComparisonSummary> suppliers) { this.suppliers = suppliers; }
    public String getComparativeAnalysis() { return comparativeAnalysis; }
    public void setComparativeAnalysis(String comparativeAnalysis) { this.comparativeAnalysis = comparativeAnalysis; }
    public Long getRecommendedSupplierId() { return recommendedSupplierId; }
    public void setRecommendedSupplierId(Long recommendedSupplierId) { this.recommendedSupplierId = recommendedSupplierId; }
    public String getRecommendedSupplierName() { return recommendedSupplierName; }
    public void setRecommendedSupplierName(String recommendedSupplierName) { this.recommendedSupplierName = recommendedSupplierName; }
    public String getRecommendationRationale() { return recommendationRationale; }
    public void setRecommendationRationale(String recommendationRationale) { this.recommendationRationale = recommendationRationale; }
    public Map<String, List<String>> getStrengthsBySupplier() { return strengthsBySupplier; }
    public void setStrengthsBySupplier(Map<String, List<String>> strengthsBySupplier) { this.strengthsBySupplier = strengthsBySupplier; }
    public Map<String, List<String>> getWeaknessesBySupplier() { return weaknessesBySupplier; }
    public void setWeaknessesBySupplier(Map<String, List<String>> weaknessesBySupplier) { this.weaknessesBySupplier = weaknessesBySupplier; }
}
