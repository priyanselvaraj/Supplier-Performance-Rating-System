package com.supplier.sprsystem.dto.response;

public class SupplierPerformanceAnalyticsResponse {

    private double averageScore;
    private double highestScore;
    private double lowestScore;
    private long totalRatedSuppliers;

    public SupplierPerformanceAnalyticsResponse() {}

    public SupplierPerformanceAnalyticsResponse(double averageScore, double highestScore, double lowestScore, long totalRatedSuppliers) {
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.totalRatedSuppliers = totalRatedSuppliers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double averageScore;
        private double highestScore;
        private double lowestScore;
        private long totalRatedSuppliers;

        public Builder averageScore(double averageScore) { this.averageScore = averageScore; return this; }
        public Builder highestScore(double highestScore) { this.highestScore = highestScore; return this; }
        public Builder lowestScore(double lowestScore) { this.lowestScore = lowestScore; return this; }
        public Builder totalRatedSuppliers(long totalRatedSuppliers) { this.totalRatedSuppliers = totalRatedSuppliers; return this; }

        public SupplierPerformanceAnalyticsResponse build() {
            return new SupplierPerformanceAnalyticsResponse(averageScore, highestScore, lowestScore, totalRatedSuppliers);
        }
    }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public double getHighestScore() { return highestScore; }
    public void setHighestScore(double highestScore) { this.highestScore = highestScore; }
    public double getLowestScore() { return lowestScore; }
    public void setLowestScore(double lowestScore) { this.lowestScore = lowestScore; }
    public long getTotalRatedSuppliers() { return totalRatedSuppliers; }
    public void setTotalRatedSuppliers(long totalRatedSuppliers) { this.totalRatedSuppliers = totalRatedSuppliers; }
}
