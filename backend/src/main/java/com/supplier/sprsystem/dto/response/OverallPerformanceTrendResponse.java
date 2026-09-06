package com.supplier.sprsystem.dto.response;

public class OverallPerformanceTrendResponse {

    private String period;
    private Double averageScore;
    private long evaluationCount;

    public OverallPerformanceTrendResponse() {}

    public OverallPerformanceTrendResponse(String period, Double averageScore, long evaluationCount) {
        this.period = period;
        this.averageScore = averageScore;
        this.evaluationCount = evaluationCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String period;
        private Double averageScore;
        private long evaluationCount;

        public Builder period(String period) { this.period = period; return this; }
        public Builder averageScore(Double averageScore) { this.averageScore = averageScore; return this; }
        public Builder evaluationCount(long evaluationCount) { this.evaluationCount = evaluationCount; return this; }

        public OverallPerformanceTrendResponse build() {
            return new OverallPerformanceTrendResponse(period, averageScore, evaluationCount);
        }
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    public long getEvaluationCount() { return evaluationCount; }
    public void setEvaluationCount(long evaluationCount) { this.evaluationCount = evaluationCount; }
}
