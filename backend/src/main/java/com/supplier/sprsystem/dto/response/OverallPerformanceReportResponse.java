package com.supplier.sprsystem.dto.response;

import java.time.LocalDate;
import java.util.List;

public class OverallPerformanceReportResponse {

    private LocalDate generatedAt;
    private long totalSuppliers;
    private long activeSuppliers;
    private long inactiveSuppliers;
    private long totalRatedSuppliers;
    private Double averagePerformanceScore;
    private Double highestPerformanceScore;
    private Double lowestPerformanceScore;
    private List<RatingDistributionResponse> ratingDistribution;
    private List<PerformanceStatusDistributionResponse> performanceStatusDistribution;
    private List<TopSupplierResponse> topSuppliers;
    private List<TopSupplierResponse> lowPerformingSuppliers;

    public OverallPerformanceReportResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate generatedAt;
        private long totalSuppliers;
        private long activeSuppliers;
        private long inactiveSuppliers;
        private long totalRatedSuppliers;
        private Double averagePerformanceScore;
        private Double highestPerformanceScore;
        private Double lowestPerformanceScore;
        private List<RatingDistributionResponse> ratingDistribution;
        private List<PerformanceStatusDistributionResponse> performanceStatusDistribution;
        private List<TopSupplierResponse> topSuppliers;
        private List<TopSupplierResponse> lowPerformingSuppliers;

        public Builder generatedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; return this; }
        public Builder totalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; return this; }
        public Builder activeSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; return this; }
        public Builder inactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; return this; }
        public Builder totalRatedSuppliers(long totalRatedSuppliers) { this.totalRatedSuppliers = totalRatedSuppliers; return this; }
        public Builder averagePerformanceScore(Double averagePerformanceScore) { this.averagePerformanceScore = averagePerformanceScore; return this; }
        public Builder highestPerformanceScore(Double highestPerformanceScore) { this.highestPerformanceScore = highestPerformanceScore; return this; }
        public Builder lowestPerformanceScore(Double lowestPerformanceScore) { this.lowestPerformanceScore = lowestPerformanceScore; return this; }
        public Builder ratingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; return this; }
        public Builder performanceStatusDistribution(List<PerformanceStatusDistributionResponse> performanceStatusDistribution) { this.performanceStatusDistribution = performanceStatusDistribution; return this; }
        public Builder topSuppliers(List<TopSupplierResponse> topSuppliers) { this.topSuppliers = topSuppliers; return this; }
        public Builder lowPerformingSuppliers(List<TopSupplierResponse> lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; return this; }

        public OverallPerformanceReportResponse build() {
            OverallPerformanceReportResponse r = new OverallPerformanceReportResponse();
            r.generatedAt = this.generatedAt;
            r.totalSuppliers = this.totalSuppliers;
            r.activeSuppliers = this.activeSuppliers;
            r.inactiveSuppliers = this.inactiveSuppliers;
            r.totalRatedSuppliers = this.totalRatedSuppliers;
            r.averagePerformanceScore = this.averagePerformanceScore;
            r.highestPerformanceScore = this.highestPerformanceScore;
            r.lowestPerformanceScore = this.lowestPerformanceScore;
            r.ratingDistribution = this.ratingDistribution;
            r.performanceStatusDistribution = this.performanceStatusDistribution;
            r.topSuppliers = this.topSuppliers;
            r.lowPerformingSuppliers = this.lowPerformingSuppliers;
            return r;
        }
    }

    public LocalDate getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; }
    public long getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; }
    public long getActiveSuppliers() { return activeSuppliers; }
    public void setActiveSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; }
    public long getInactiveSuppliers() { return inactiveSuppliers; }
    public void setInactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; }
    public long getTotalRatedSuppliers() { return totalRatedSuppliers; }
    public void setTotalRatedSuppliers(long totalRatedSuppliers) { this.totalRatedSuppliers = totalRatedSuppliers; }
    public Double getAveragePerformanceScore() { return averagePerformanceScore; }
    public void setAveragePerformanceScore(Double averagePerformanceScore) { this.averagePerformanceScore = averagePerformanceScore; }
    public Double getHighestPerformanceScore() { return highestPerformanceScore; }
    public void setHighestPerformanceScore(Double highestPerformanceScore) { this.highestPerformanceScore = highestPerformanceScore; }
    public Double getLowestPerformanceScore() { return lowestPerformanceScore; }
    public void setLowestPerformanceScore(Double lowestPerformanceScore) { this.lowestPerformanceScore = lowestPerformanceScore; }
    public List<RatingDistributionResponse> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
    public List<PerformanceStatusDistributionResponse> getPerformanceStatusDistribution() { return performanceStatusDistribution; }
    public void setPerformanceStatusDistribution(List<PerformanceStatusDistributionResponse> performanceStatusDistribution) { this.performanceStatusDistribution = performanceStatusDistribution; }
    public List<TopSupplierResponse> getTopSuppliers() { return topSuppliers; }
    public void setTopSuppliers(List<TopSupplierResponse> topSuppliers) { this.topSuppliers = topSuppliers; }
    public List<TopSupplierResponse> getLowPerformingSuppliers() { return lowPerformingSuppliers; }
    public void setLowPerformingSuppliers(List<TopSupplierResponse> lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; }
}
