package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.util.List;

public class SupplierPortalPerformanceResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Double currentScore;
    private SupplierRating currentRating;
    private RatingCategory ratingCategory;
    private PerformanceStatus performanceStatus;
    private PerformanceTrend performanceTrend;
    private Double scoreDifference;
    private Integer totalEvaluations;

    private List<SupplierPerformanceRatingResponse> ratingHistory;
    private List<PerformanceTrendResponse> trendData;
    private SupplierAiInsightsResponse aiInsights;

    public SupplierPortalPerformanceResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private Double currentScore;
        private SupplierRating currentRating;
        private RatingCategory ratingCategory;
        private PerformanceStatus performanceStatus;
        private PerformanceTrend performanceTrend;
        private Double scoreDifference;
        private Integer totalEvaluations;
        private List<SupplierPerformanceRatingResponse> ratingHistory;
        private List<PerformanceTrendResponse> trendData;
        private SupplierAiInsightsResponse aiInsights;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder currentScore(Double currentScore) { this.currentScore = currentScore; return this; }
        public Builder currentRating(SupplierRating currentRating) { this.currentRating = currentRating; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder performanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; return this; }
        public Builder performanceTrend(PerformanceTrend performanceTrend) { this.performanceTrend = performanceTrend; return this; }
        public Builder scoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder ratingHistory(List<SupplierPerformanceRatingResponse> ratingHistory) { this.ratingHistory = ratingHistory; return this; }
        public Builder trendData(List<PerformanceTrendResponse> trendData) { this.trendData = trendData; return this; }
        public Builder aiInsights(SupplierAiInsightsResponse aiInsights) { this.aiInsights = aiInsights; return this; }

        public SupplierPortalPerformanceResponse build() {
            SupplierPortalPerformanceResponse r = new SupplierPortalPerformanceResponse();
            r.supplierId = this.supplierId;
            r.supplierCode = this.supplierCode;
            r.supplierName = this.supplierName;
            r.currentScore = this.currentScore;
            r.currentRating = this.currentRating;
            r.ratingCategory = this.ratingCategory;
            r.performanceStatus = this.performanceStatus;
            r.performanceTrend = this.performanceTrend;
            r.scoreDifference = this.scoreDifference;
            r.totalEvaluations = this.totalEvaluations;
            r.ratingHistory = this.ratingHistory;
            r.trendData = this.trendData;
            r.aiInsights = this.aiInsights;
            return r;
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    public SupplierRating getCurrentRating() { return currentRating; }
    public void setCurrentRating(SupplierRating currentRating) { this.currentRating = currentRating; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; }
    public PerformanceTrend getPerformanceTrend() { return performanceTrend; }
    public void setPerformanceTrend(PerformanceTrend performanceTrend) { this.performanceTrend = performanceTrend; }
    public Double getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; }
    public Integer getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public List<SupplierPerformanceRatingResponse> getRatingHistory() { return ratingHistory; }
    public void setRatingHistory(List<SupplierPerformanceRatingResponse> ratingHistory) { this.ratingHistory = ratingHistory; }
    public List<PerformanceTrendResponse> getTrendData() { return trendData; }
    public void setTrendData(List<PerformanceTrendResponse> trendData) { this.trendData = trendData; }
    public SupplierAiInsightsResponse getAiInsights() { return aiInsights; }
    public void setAiInsights(SupplierAiInsightsResponse aiInsights) { this.aiInsights = aiInsights; }
}
