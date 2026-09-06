package com.supplier.sprsystem.dto.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiDashboardResponse {

    private Long totalSuppliers;
    private Long activeSuppliers;
    private Double averageSupplierScore;
    private Long totalEvaluations;
    private Long highRiskSuppliersCount;
    private Long improvingSuppliersCount;
    private Long decliningSuppliersCount;
    private Long openImprovementActionsCount;
    private Long pendingApprovalsCount;
    private Long overdueWorkflowsCount;
    private Double slaComplianceRate;

    private List<KpiCalculationResultResponse> topKpis = new ArrayList<>();
    private List<RatingDistributionResponse> ratingDistribution = new ArrayList<>();
    private List<PerformanceStatusDistributionResponse> performanceDistribution = new ArrayList<>();
    private List<OverallPerformanceTrendResponse> performanceTrends = new ArrayList<>();
    private List<CategorySupplierStatisticsResponse> categoryBreakdown = new ArrayList<>();
    private List<TopSupplierResponse> topPerformingSuppliers = new ArrayList<>();
    private List<TopSupplierResponse> lowPerformingSuppliers = new ArrayList<>();
    private Map<String, Object> operationalHealth = new HashMap<>();

    public BiDashboardResponse() {}

    public BiDashboardResponse(Long totalSuppliers, Long activeSuppliers, Double averageSupplierScore, Long totalEvaluations, Long highRiskSuppliersCount, Long improvingSuppliersCount, Long decliningSuppliersCount, Long openImprovementActionsCount, Long pendingApprovalsCount, Long overdueWorkflowsCount, Double slaComplianceRate, List<KpiCalculationResultResponse> topKpis, List<RatingDistributionResponse> ratingDistribution, List<PerformanceStatusDistributionResponse> performanceDistribution, List<OverallPerformanceTrendResponse> performanceTrends, List<CategorySupplierStatisticsResponse> categoryBreakdown, List<TopSupplierResponse> topPerformingSuppliers, List<TopSupplierResponse> lowPerformingSuppliers, Map<String, Object> operationalHealth) {
        this.totalSuppliers = totalSuppliers;
        this.activeSuppliers = activeSuppliers;
        this.averageSupplierScore = averageSupplierScore;
        this.totalEvaluations = totalEvaluations;
        this.highRiskSuppliersCount = highRiskSuppliersCount;
        this.improvingSuppliersCount = improvingSuppliersCount;
        this.decliningSuppliersCount = decliningSuppliersCount;
        this.openImprovementActionsCount = openImprovementActionsCount;
        this.pendingApprovalsCount = pendingApprovalsCount;
        this.overdueWorkflowsCount = overdueWorkflowsCount;
        this.slaComplianceRate = slaComplianceRate;
        this.topKpis = topKpis != null ? topKpis : new ArrayList<>();
        this.ratingDistribution = ratingDistribution != null ? ratingDistribution : new ArrayList<>();
        this.performanceDistribution = performanceDistribution != null ? performanceDistribution : new ArrayList<>();
        this.performanceTrends = performanceTrends != null ? performanceTrends : new ArrayList<>();
        this.categoryBreakdown = categoryBreakdown != null ? categoryBreakdown : new ArrayList<>();
        this.topPerformingSuppliers = topPerformingSuppliers != null ? topPerformingSuppliers : new ArrayList<>();
        this.lowPerformingSuppliers = lowPerformingSuppliers != null ? lowPerformingSuppliers : new ArrayList<>();
        this.operationalHealth = operationalHealth != null ? operationalHealth : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long totalSuppliers;
        private Long activeSuppliers;
        private Double averageSupplierScore;
        private Long totalEvaluations;
        private Long highRiskSuppliersCount;
        private Long improvingSuppliersCount;
        private Long decliningSuppliersCount;
        private Long openImprovementActionsCount;
        private Long pendingApprovalsCount;
        private Long overdueWorkflowsCount;
        private Double slaComplianceRate;
        private List<KpiCalculationResultResponse> topKpis = new ArrayList<>();
        private List<RatingDistributionResponse> ratingDistribution = new ArrayList<>();
        private List<PerformanceStatusDistributionResponse> performanceDistribution = new ArrayList<>();
        private List<OverallPerformanceTrendResponse> performanceTrends = new ArrayList<>();
        private List<CategorySupplierStatisticsResponse> categoryBreakdown = new ArrayList<>();
        private List<TopSupplierResponse> topPerformingSuppliers = new ArrayList<>();
        private List<TopSupplierResponse> lowPerformingSuppliers = new ArrayList<>();
        private Map<String, Object> operationalHealth = new HashMap<>();

        public Builder totalSuppliers(Long totalSuppliers) { this.totalSuppliers = totalSuppliers; return this; }
        public Builder activeSuppliers(Long activeSuppliers) { this.activeSuppliers = activeSuppliers; return this; }
        public Builder averageSupplierScore(Double averageSupplierScore) { this.averageSupplierScore = averageSupplierScore; return this; }
        public Builder totalEvaluations(Long totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder highRiskSuppliersCount(Long highRiskSuppliersCount) { this.highRiskSuppliersCount = highRiskSuppliersCount; return this; }
        public Builder improvingSuppliersCount(Long improvingSuppliersCount) { this.improvingSuppliersCount = improvingSuppliersCount; return this; }
        public Builder decliningSuppliersCount(Long decliningSuppliersCount) { this.decliningSuppliersCount = decliningSuppliersCount; return this; }
        public Builder openImprovementActionsCount(Long openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; return this; }
        public Builder pendingApprovalsCount(Long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; return this; }
        public Builder overdueWorkflowsCount(Long overdueWorkflowsCount) { this.overdueWorkflowsCount = overdueWorkflowsCount; return this; }
        public Builder slaComplianceRate(Double slaComplianceRate) { this.slaComplianceRate = slaComplianceRate; return this; }
        public Builder topKpis(List<KpiCalculationResultResponse> topKpis) { this.topKpis = topKpis; return this; }
        public Builder ratingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; return this; }
        public Builder performanceDistribution(List<PerformanceStatusDistributionResponse> performanceDistribution) { this.performanceDistribution = performanceDistribution; return this; }
        public Builder performanceTrends(List<OverallPerformanceTrendResponse> performanceTrends) { this.performanceTrends = performanceTrends; return this; }
        public Builder categoryBreakdown(List<CategorySupplierStatisticsResponse> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; return this; }
        public Builder topPerformingSuppliers(List<TopSupplierResponse> topPerformingSuppliers) { this.topPerformingSuppliers = topPerformingSuppliers; return this; }
        public Builder lowPerformingSuppliers(List<TopSupplierResponse> lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; return this; }
        public Builder operationalHealth(Map<String, Object> operationalHealth) { this.operationalHealth = operationalHealth; return this; }

        public BiDashboardResponse build() {
            return new BiDashboardResponse(totalSuppliers, activeSuppliers, averageSupplierScore, totalEvaluations, highRiskSuppliersCount, improvingSuppliersCount, decliningSuppliersCount, openImprovementActionsCount, pendingApprovalsCount, overdueWorkflowsCount, slaComplianceRate, topKpis, ratingDistribution, performanceDistribution, performanceTrends, categoryBreakdown, topPerformingSuppliers, lowPerformingSuppliers, operationalHealth);
        }
    }

    public Long getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(Long totalSuppliers) { this.totalSuppliers = totalSuppliers; }
    public Long getActiveSuppliers() { return activeSuppliers; }
    public void setActiveSuppliers(Long activeSuppliers) { this.activeSuppliers = activeSuppliers; }
    public Double getAverageSupplierScore() { return averageSupplierScore; }
    public void setAverageSupplierScore(Double averageSupplierScore) { this.averageSupplierScore = averageSupplierScore; }
    public Long getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Long totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public Long getHighRiskSuppliersCount() { return highRiskSuppliersCount; }
    public void setHighRiskSuppliersCount(Long highRiskSuppliersCount) { this.highRiskSuppliersCount = highRiskSuppliersCount; }
    public Long getImprovingSuppliersCount() { return improvingSuppliersCount; }
    public void setImprovingSuppliersCount(Long improvingSuppliersCount) { this.improvingSuppliersCount = improvingSuppliersCount; }
    public Long getDecliningSuppliersCount() { return decliningSuppliersCount; }
    public void setDecliningSuppliersCount(Long decliningSuppliersCount) { this.decliningSuppliersCount = decliningSuppliersCount; }
    public Long getOpenImprovementActionsCount() { return openImprovementActionsCount; }
    public void setOpenImprovementActionsCount(Long openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; }
    public Long getPendingApprovalsCount() { return pendingApprovalsCount; }
    public void setPendingApprovalsCount(Long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; }
    public Long getOverdueWorkflowsCount() { return overdueWorkflowsCount; }
    public void setOverdueWorkflowsCount(Long overdueWorkflowsCount) { this.overdueWorkflowsCount = overdueWorkflowsCount; }
    public Double getSlaComplianceRate() { return slaComplianceRate; }
    public void setSlaComplianceRate(Double slaComplianceRate) { this.slaComplianceRate = slaComplianceRate; }
    public List<KpiCalculationResultResponse> getTopKpis() { return topKpis; }
    public void setTopKpis(List<KpiCalculationResultResponse> topKpis) { this.topKpis = topKpis; }
    public List<RatingDistributionResponse> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
    public List<PerformanceStatusDistributionResponse> getPerformanceDistribution() { return performanceDistribution; }
    public void setPerformanceDistribution(List<PerformanceStatusDistributionResponse> performanceDistribution) { this.performanceDistribution = performanceDistribution; }
    public List<OverallPerformanceTrendResponse> getPerformanceTrends() { return performanceTrends; }
    public void setPerformanceTrends(List<OverallPerformanceTrendResponse> performanceTrends) { this.performanceTrends = performanceTrends; }
    public List<CategorySupplierStatisticsResponse> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(List<CategorySupplierStatisticsResponse> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }
    public List<TopSupplierResponse> getTopPerformingSuppliers() { return topPerformingSuppliers; }
    public void setTopPerformingSuppliers(List<TopSupplierResponse> topPerformingSuppliers) { this.topPerformingSuppliers = topPerformingSuppliers; }
    public List<TopSupplierResponse> getLowPerformingSuppliers() { return lowPerformingSuppliers; }
    public void setLowPerformingSuppliers(List<TopSupplierResponse> lowPerformingSuppliers) { this.lowPerformingSuppliers = lowPerformingSuppliers; }
    public Map<String, Object> getOperationalHealth() { return operationalHealth; }
    public void setOperationalHealth(Map<String, Object> operationalHealth) { this.operationalHealth = operationalHealth; }
}
