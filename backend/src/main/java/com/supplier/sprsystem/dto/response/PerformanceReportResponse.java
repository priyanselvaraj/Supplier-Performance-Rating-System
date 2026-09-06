package com.supplier.sprsystem.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PerformanceReportResponse {

    private LocalDate generatedAt;
    private long totalSuppliersEvaluated;
    private Double overallSystemAverageScore;
    private Map<String, Long> categoryCountBreakdown;
    private Map<String, Long> ratingCategoryBreakdown;
    private List<SupplierResponse> suppliers;
    private List<EvaluationResponse> evaluations;

    public PerformanceReportResponse() {}

    public PerformanceReportResponse(LocalDate generatedAt, long totalSuppliersEvaluated, Double overallSystemAverageScore, Map<String, Long> categoryCountBreakdown, Map<String, Long> ratingCategoryBreakdown, List<SupplierResponse> suppliers, List<EvaluationResponse> evaluations) {
        this.generatedAt = generatedAt;
        this.totalSuppliersEvaluated = totalSuppliersEvaluated;
        this.overallSystemAverageScore = overallSystemAverageScore;
        this.categoryCountBreakdown = categoryCountBreakdown;
        this.ratingCategoryBreakdown = ratingCategoryBreakdown;
        this.suppliers = suppliers;
        this.evaluations = evaluations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate generatedAt;
        private long totalSuppliersEvaluated;
        private Double overallSystemAverageScore;
        private Map<String, Long> categoryCountBreakdown;
        private Map<String, Long> ratingCategoryBreakdown;
        private List<SupplierResponse> suppliers;
        private List<EvaluationResponse> evaluations;

        public Builder generatedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; return this; }
        public Builder totalSuppliersEvaluated(long totalSuppliersEvaluated) { this.totalSuppliersEvaluated = totalSuppliersEvaluated; return this; }
        public Builder overallSystemAverageScore(Double overallSystemAverageScore) { this.overallSystemAverageScore = overallSystemAverageScore; return this; }
        public Builder categoryCountBreakdown(Map<String, Long> categoryCountBreakdown) { this.categoryCountBreakdown = categoryCountBreakdown; return this; }
        public Builder ratingCategoryBreakdown(Map<String, Long> ratingCategoryBreakdown) { this.ratingCategoryBreakdown = ratingCategoryBreakdown; return this; }
        public Builder suppliers(List<SupplierResponse> suppliers) { this.suppliers = suppliers; return this; }
        public Builder evaluations(List<EvaluationResponse> evaluations) { this.evaluations = evaluations; return this; }

        public PerformanceReportResponse build() {
            return new PerformanceReportResponse(generatedAt, totalSuppliersEvaluated, overallSystemAverageScore, categoryCountBreakdown, ratingCategoryBreakdown, suppliers, evaluations);
        }
    }

    public LocalDate getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; }
    public long getTotalSuppliersEvaluated() { return totalSuppliersEvaluated; }
    public void setTotalSuppliersEvaluated(long totalSuppliersEvaluated) { this.totalSuppliersEvaluated = totalSuppliersEvaluated; }
    public Double getOverallSystemAverageScore() { return overallSystemAverageScore; }
    public void setOverallSystemAverageScore(Double overallSystemAverageScore) { this.overallSystemAverageScore = overallSystemAverageScore; }
    public Map<String, Long> getCategoryCountBreakdown() { return categoryCountBreakdown; }
    public void setCategoryCountBreakdown(Map<String, Long> categoryCountBreakdown) { this.categoryCountBreakdown = categoryCountBreakdown; }
    public Map<String, Long> getRatingCategoryBreakdown() { return ratingCategoryBreakdown; }
    public void setRatingCategoryBreakdown(Map<String, Long> ratingCategoryBreakdown) { this.ratingCategoryBreakdown = ratingCategoryBreakdown; }
    public List<SupplierResponse> getSuppliers() { return suppliers; }
    public void setSuppliers(List<SupplierResponse> suppliers) { this.suppliers = suppliers; }
    public List<EvaluationResponse> getEvaluations() { return evaluations; }
    public void setEvaluations(List<EvaluationResponse> evaluations) { this.evaluations = evaluations; }
}
