package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceTrend;

import java.util.List;

public class SupplierAiTrendResponse {
    private Long supplierId;
    private String supplierName;
    private PerformanceTrend trend;
    private Double previousScore;
    private Double currentScore;
    private Double scoreDifference;
    private Double percentageChange;
    private List<Double> recentScores;
    private List<String> evaluationPeriods;
    private String explanation;

    public SupplierAiTrendResponse() {}

    public SupplierAiTrendResponse(Long supplierId, String supplierName, PerformanceTrend trend, Double previousScore, Double currentScore, Double scoreDifference, Double percentageChange, List<Double> recentScores, List<String> evaluationPeriods, String explanation) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.trend = trend;
        this.previousScore = previousScore;
        this.currentScore = currentScore;
        this.scoreDifference = scoreDifference;
        this.percentageChange = percentageChange;
        this.recentScores = recentScores;
        this.evaluationPeriods = evaluationPeriods;
        this.explanation = explanation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierName;
        private PerformanceTrend trend;
        private Double previousScore;
        private Double currentScore;
        private Double scoreDifference;
        private Double percentageChange;
        private List<Double> recentScores;
        private List<String> evaluationPeriods;
        private String explanation;

        public Builder supplierId(Long id) { this.supplierId = id; return this; }
        public Builder supplierName(String name) { this.supplierName = name; return this; }
        public Builder trend(PerformanceTrend trend) { this.trend = trend; return this; }
        public Builder previousScore(Double score) { this.previousScore = score; return this; }
        public Builder currentScore(Double score) { this.currentScore = score; return this; }
        public Builder scoreDifference(Double diff) { this.scoreDifference = diff; return this; }
        public Builder percentageChange(Double pct) { this.percentageChange = pct; return this; }
        public Builder recentScores(List<Double> scores) { this.recentScores = scores; return this; }
        public Builder evaluationPeriods(List<String> periods) { this.evaluationPeriods = periods; return this; }
        public Builder explanation(String exp) { this.explanation = exp; return this; }

        public SupplierAiTrendResponse build() {
            return new SupplierAiTrendResponse(supplierId, supplierName, trend, previousScore, currentScore, scoreDifference, percentageChange, recentScores, evaluationPeriods, explanation);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public PerformanceTrend getTrend() { return trend; }
    public void setTrend(PerformanceTrend trend) { this.trend = trend; }
    public Double getPreviousScore() { return previousScore; }
    public void setPreviousScore(Double previousScore) { this.previousScore = previousScore; }
    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    public Double getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; }
    public Double getPercentageChange() { return percentageChange; }
    public void setPercentageChange(Double percentageChange) { this.percentageChange = percentageChange; }
    public List<Double> getRecentScores() { return recentScores; }
    public void setRecentScores(List<Double> recentScores) { this.recentScores = recentScores; }
    public List<String> getEvaluationPeriods() { return evaluationPeriods; }
    public void setEvaluationPeriods(List<String> evaluationPeriods) { this.evaluationPeriods = evaluationPeriods; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
