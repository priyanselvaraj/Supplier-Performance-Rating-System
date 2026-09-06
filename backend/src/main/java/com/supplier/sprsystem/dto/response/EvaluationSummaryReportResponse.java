package com.supplier.sprsystem.dto.response;

import java.time.LocalDate;

public class EvaluationSummaryReportResponse {

    private LocalDate generatedAt;
    private long totalEvaluations;
    private long draftEvaluations;
    private long submittedEvaluations;
    private long completedEvaluations;
    private long cancelledEvaluations;
    private Double averageEvaluationScore;

    public EvaluationSummaryReportResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate generatedAt;
        private long totalEvaluations;
        private long draftEvaluations;
        private long submittedEvaluations;
        private long completedEvaluations;
        private long cancelledEvaluations;
        private Double averageEvaluationScore;

        public Builder generatedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; return this; }
        public Builder totalEvaluations(long totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder draftEvaluations(long draftEvaluations) { this.draftEvaluations = draftEvaluations; return this; }
        public Builder submittedEvaluations(long submittedEvaluations) { this.submittedEvaluations = submittedEvaluations; return this; }
        public Builder completedEvaluations(long completedEvaluations) { this.completedEvaluations = completedEvaluations; return this; }
        public Builder cancelledEvaluations(long cancelledEvaluations) { this.cancelledEvaluations = cancelledEvaluations; return this; }
        public Builder averageEvaluationScore(Double averageEvaluationScore) { this.averageEvaluationScore = averageEvaluationScore; return this; }

        public EvaluationSummaryReportResponse build() {
            EvaluationSummaryReportResponse r = new EvaluationSummaryReportResponse();
            r.generatedAt = this.generatedAt;
            r.totalEvaluations = this.totalEvaluations;
            r.draftEvaluations = this.draftEvaluations;
            r.submittedEvaluations = this.submittedEvaluations;
            r.completedEvaluations = this.completedEvaluations;
            r.cancelledEvaluations = this.cancelledEvaluations;
            r.averageEvaluationScore = this.averageEvaluationScore;
            return r;
        }
    }

    public LocalDate getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; }
    public long getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(long totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public long getDraftEvaluations() { return draftEvaluations; }
    public void setDraftEvaluations(long draftEvaluations) { this.draftEvaluations = draftEvaluations; }
    public long getSubmittedEvaluations() { return submittedEvaluations; }
    public void setSubmittedEvaluations(long submittedEvaluations) { this.submittedEvaluations = submittedEvaluations; }
    public long getCompletedEvaluations() { return completedEvaluations; }
    public void setCompletedEvaluations(long completedEvaluations) { this.completedEvaluations = completedEvaluations; }
    public long getCancelledEvaluations() { return cancelledEvaluations; }
    public void setCancelledEvaluations(long cancelledEvaluations) { this.cancelledEvaluations = cancelledEvaluations; }
    public Double getAverageEvaluationScore() { return averageEvaluationScore; }
    public void setAverageEvaluationScore(Double averageEvaluationScore) { this.averageEvaluationScore = averageEvaluationScore; }
}
