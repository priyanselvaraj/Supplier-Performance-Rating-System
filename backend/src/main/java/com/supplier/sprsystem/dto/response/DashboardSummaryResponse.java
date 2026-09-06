package com.supplier.sprsystem.dto.response;

import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private long totalSuppliers;
    private long activeSuppliers;
    private long inactiveSuppliers;
    private long pendingSuppliers;
    private long totalEvaluations;
    private long draftEvaluations;
    private long submittedEvaluations;
    private long completedEvaluations;
    private long cancelledEvaluations;
    private Double averagePerformanceScore;
    private Double averageSupplierRating;
    private long highPerformingSuppliers;
    private long satisfactorySuppliers;
    private long needsImprovementSuppliers;
    private long lowPerformingSuppliers;

    private long excellentSuppliersCount;
    private long goodSuppliersCount;
    private long averageSuppliersCount;
    private long poorSuppliersCount;
    private long unratedSuppliersCount;

    private Map<String, Long> ratingDistribution;
    private List<SupplierResponse> topPerformingSuppliers;
    private List<SupplierResponse> lowPerformingSuppliersList;
    private List<EvaluationResponse> recentEvaluations;

    public DashboardSummaryResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalSuppliers;
        private long activeSuppliers;
        private long inactiveSuppliers;
        private long pendingSuppliers;
        private long totalEvaluations;
        private long draftEvaluations;
        private long submittedEvaluations;
        private long completedEvaluations;
        private long cancelledEvaluations;
        private Double averagePerformanceScore = 0.0;
        private Double averageSupplierRating = 0.0;
        private long highPerformingSuppliers;
        private long satisfactorySuppliers;
        private long needsImprovementSuppliers;
        private long lowPerformingSuppliers;

        private long excellentSuppliersCount;
        private long goodSuppliersCount;
        private long averageSuppliersCount;
        private long poorSuppliersCount;
        private long unratedSuppliersCount;

        private Map<String, Long> ratingDistribution;
        private List<SupplierResponse> topPerformingSuppliers;
        private List<SupplierResponse> lowPerformingSuppliersList;
        private List<EvaluationResponse> recentEvaluations;

        public Builder totalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; return this; }
        public Builder activeSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; return this; }
        public Builder inactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; return this; }
        public Builder pendingSuppliers(long pendingSuppliers) { this.pendingSuppliers = pendingSuppliers; return this; }
        public Builder totalEvaluations(long totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder draftEvaluations(long draftEvaluations) { this.draftEvaluations = draftEvaluations; return this; }
        public Builder submittedEvaluations(long submittedEvaluations) { this.submittedEvaluations = submittedEvaluations; return this; }
        public Builder completedEvaluations(long completedEvaluations) { this.completedEvaluations = completedEvaluations; return this; }
        public Builder cancelledEvaluations(long cancelledEvaluations) { this.cancelledEvaluations = cancelledEvaluations; return this; }
        public Builder averagePerformanceScore(Double averagePerformanceScore) {
            this.averagePerformanceScore = averagePerformanceScore;
            this.averageSupplierRating = averagePerformanceScore;
            return this;
        }
        public Builder averageSupplierRating(Double averageSupplierRating) {
            this.averageSupplierRating = averageSupplierRating;
            this.averagePerformanceScore = averageSupplierRating;
            return this;
        }
        public Builder highPerformingSuppliers(long highPerformingSuppliers) { this.highPerformingSuppliers = highPerformingSuppliers; return this; }
        public Builder satisfactorySuppliers(long satisfactorySuppliers) { this.satisfactorySuppliers = satisfactorySuppliers; return this; }
        public Builder needsImprovementSuppliers(long needsImprovementSuppliers) { this.needsImprovementSuppliers = needsImprovementSuppliers; return this; }
        public Builder lowPerformingSuppliers(long lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; return this; }

        public Builder excellentSuppliersCount(long excellentSuppliersCount) { this.excellentSuppliersCount = excellentSuppliersCount; return this; }
        public Builder goodSuppliersCount(long goodSuppliersCount) { this.goodSuppliersCount = goodSuppliersCount; return this; }
        public Builder averageSuppliersCount(long averageSuppliersCount) { this.averageSuppliersCount = averageSuppliersCount; return this; }
        public Builder poorSuppliersCount(long poorSuppliersCount) { this.poorSuppliersCount = poorSuppliersCount; return this; }
        public Builder unratedSuppliersCount(long unratedSuppliersCount) { this.unratedSuppliersCount = unratedSuppliersCount; return this; }

        public Builder ratingDistribution(Map<String, Long> ratingDistribution) { this.ratingDistribution = ratingDistribution; return this; }
        public Builder topPerformingSuppliers(List<SupplierResponse> topPerformingSuppliers) { this.topPerformingSuppliers = topPerformingSuppliers; return this; }
        public Builder lowPerformingSuppliers(List<SupplierResponse> lowPerformingSuppliersList) { this.lowPerformingSuppliersList = lowPerformingSuppliersList; return this; }
        public Builder lowPerformingSuppliersList(List<SupplierResponse> lowPerformingSuppliersList) { this.lowPerformingSuppliersList = lowPerformingSuppliersList; return this; }
        public Builder recentEvaluations(List<EvaluationResponse> recentEvaluations) { this.recentEvaluations = recentEvaluations; return this; }

        public DashboardSummaryResponse build() {
            DashboardSummaryResponse r = new DashboardSummaryResponse();
            r.totalSuppliers = this.totalSuppliers;
            r.activeSuppliers = this.activeSuppliers;
            r.inactiveSuppliers = this.inactiveSuppliers;
            r.pendingSuppliers = this.pendingSuppliers;
            r.totalEvaluations = this.totalEvaluations;
            r.draftEvaluations = this.draftEvaluations;
            r.submittedEvaluations = this.submittedEvaluations;
            r.completedEvaluations = this.completedEvaluations;
            r.cancelledEvaluations = this.cancelledEvaluations;
            r.averagePerformanceScore = this.averagePerformanceScore != null ? this.averagePerformanceScore : 0.0;
            r.averageSupplierRating = this.averageSupplierRating != null ? this.averageSupplierRating : 0.0;
            r.highPerformingSuppliers = this.highPerformingSuppliers;
            r.satisfactorySuppliers = this.satisfactorySuppliers;
            r.needsImprovementSuppliers = this.needsImprovementSuppliers;
            r.lowPerformingSuppliers = this.lowPerformingSuppliers;
            r.excellentSuppliersCount = this.excellentSuppliersCount;
            r.goodSuppliersCount = this.goodSuppliersCount;
            r.averageSuppliersCount = this.averageSuppliersCount;
            r.poorSuppliersCount = this.poorSuppliersCount;
            r.unratedSuppliersCount = this.unratedSuppliersCount;
            r.ratingDistribution = this.ratingDistribution;
            r.topPerformingSuppliers = this.topPerformingSuppliers;
            r.lowPerformingSuppliersList = this.lowPerformingSuppliersList;
            r.recentEvaluations = this.recentEvaluations;
            return r;
        }
    }

    public long getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; }
    public long getActiveSuppliers() { return activeSuppliers; }
    public void setActiveSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; }
    public long getInactiveSuppliers() { return inactiveSuppliers; }
    public void setInactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; }
    public long getPendingSuppliers() { return pendingSuppliers; }
    public void setPendingSuppliers(long pendingSuppliers) { this.pendingSuppliers = pendingSuppliers; }
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
    public Double getAveragePerformanceScore() { return averagePerformanceScore != null ? averagePerformanceScore : averageSupplierRating; }
    public void setAveragePerformanceScore(Double averagePerformanceScore) {
        this.averagePerformanceScore = averagePerformanceScore;
        this.averageSupplierRating = averagePerformanceScore;
    }
    public Double getAverageSupplierRating() { return getAveragePerformanceScore(); }
    public void setAverageSupplierRating(Double averageSupplierRating) {
        setAveragePerformanceScore(averageSupplierRating);
    }
    public long getHighPerformingSuppliers() { return highPerformingSuppliers; }
    public void setHighPerformingSuppliers(long highPerformingSuppliers) { this.highPerformingSuppliers = highPerformingSuppliers; }
    public long getSatisfactorySuppliers() { return satisfactorySuppliers; }
    public void setSatisfactorySuppliers(long satisfactorySuppliers) { this.satisfactorySuppliers = satisfactorySuppliers; }
    public long getNeedsImprovementSuppliers() { return needsImprovementSuppliers; }
    public void setNeedsImprovementSuppliers(long needsImprovementSuppliers) { this.needsImprovementSuppliers = needsImprovementSuppliers; }
    public long getLowPerformingSuppliers() { return lowPerformingSuppliers; }
    public void setLowPerformingSuppliers(long lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; }
    public long getExcellentSuppliersCount() { return excellentSuppliersCount; }
    public void setExcellentSuppliersCount(long excellentSuppliersCount) { this.excellentSuppliersCount = excellentSuppliersCount; }
    public long getGoodSuppliersCount() { return goodSuppliersCount; }
    public void setGoodSuppliersCount(long goodSuppliersCount) { this.goodSuppliersCount = goodSuppliersCount; }
    public long getAverageSuppliersCount() { return averageSuppliersCount; }
    public void setAverageSuppliersCount(long averageSuppliersCount) { this.averageSuppliersCount = averageSuppliersCount; }
    public long getPoorSuppliersCount() { return poorSuppliersCount; }
    public void setPoorSuppliersCount(long poorSuppliersCount) { this.poorSuppliersCount = poorSuppliersCount; }
    public long getUnratedSuppliersCount() { return unratedSuppliersCount; }
    public void setUnratedSuppliersCount(long unratedSuppliersCount) { this.unratedSuppliersCount = unratedSuppliersCount; }
    public Map<String, Long> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(Map<String, Long> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
    public List<SupplierResponse> getTopPerformingSuppliers() { return topPerformingSuppliers; }
    public void setTopPerformingSuppliers(List<SupplierResponse> topPerformingSuppliers) { this.topPerformingSuppliers = topPerformingSuppliers; }
    public List<SupplierResponse> getLowPerformingSuppliersList() { return lowPerformingSuppliersList; }
    public void setLowPerformingSuppliersList(List<SupplierResponse> lowPerformingSuppliersList) { this.lowPerformingSuppliersList = lowPerformingSuppliersList; }
    public List<EvaluationResponse> getRecentEvaluations() { return recentEvaluations; }
    public void setRecentEvaluations(List<EvaluationResponse> recentEvaluations) { this.recentEvaluations = recentEvaluations; }
}
