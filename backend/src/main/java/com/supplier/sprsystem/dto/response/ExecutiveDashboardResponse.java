package com.supplier.sprsystem.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ExecutiveDashboardResponse {

    private SupplierOverviewSection supplierOverview;
    private RiskOverviewSection riskOverview;
    private PerformanceOverviewSection performanceOverview;
    private OperationalOverviewSection operationalOverview;
    private List<StrategicInsightItem> strategicInsights = new ArrayList<>();

    public ExecutiveDashboardResponse() {}

    public ExecutiveDashboardResponse(SupplierOverviewSection supplierOverview, RiskOverviewSection riskOverview, PerformanceOverviewSection performanceOverview, OperationalOverviewSection operationalOverview, List<StrategicInsightItem> strategicInsights) {
        this.supplierOverview = supplierOverview;
        this.riskOverview = riskOverview;
        this.performanceOverview = performanceOverview;
        this.operationalOverview = operationalOverview;
        this.strategicInsights = strategicInsights != null ? strategicInsights : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SupplierOverviewSection supplierOverview;
        private RiskOverviewSection riskOverview;
        private PerformanceOverviewSection performanceOverview;
        private OperationalOverviewSection operationalOverview;
        private List<StrategicInsightItem> strategicInsights = new ArrayList<>();

        public Builder supplierOverview(SupplierOverviewSection supplierOverview) { this.supplierOverview = supplierOverview; return this; }
        public Builder riskOverview(RiskOverviewSection riskOverview) { this.riskOverview = riskOverview; return this; }
        public Builder performanceOverview(PerformanceOverviewSection performanceOverview) { this.performanceOverview = performanceOverview; return this; }
        public Builder operationalOverview(OperationalOverviewSection operationalOverview) { this.operationalOverview = operationalOverview; return this; }
        public Builder strategicInsights(List<StrategicInsightItem> strategicInsights) { this.strategicInsights = strategicInsights; return this; }

        public ExecutiveDashboardResponse build() {
            return new ExecutiveDashboardResponse(supplierOverview, riskOverview, performanceOverview, operationalOverview, strategicInsights);
        }
    }

    public static class SupplierOverviewSection {
        private Long totalSuppliers;
        private Long activeSuppliers;
        private Long newSuppliersLast30Days;
        private Double overallAverageRating;
        private String topCategory;
        private Double categoryHealthScore;

        public SupplierOverviewSection() {}

        public SupplierOverviewSection(Long totalSuppliers, Long activeSuppliers, Long newSuppliersLast30Days, Double overallAverageRating, String topCategory, Double categoryHealthScore) {
            this.totalSuppliers = totalSuppliers;
            this.activeSuppliers = activeSuppliers;
            this.newSuppliersLast30Days = newSuppliersLast30Days;
            this.overallAverageRating = overallAverageRating;
            this.topCategory = topCategory;
            this.categoryHealthScore = categoryHealthScore;
        }

        public static SectionBuilder builder() {
            return new SectionBuilder();
        }

        public static class SectionBuilder {
            private Long totalSuppliers;
            private Long activeSuppliers;
            private Long newSuppliersLast30Days;
            private Double overallAverageRating;
            private String topCategory;
            private Double categoryHealthScore;

            public SectionBuilder totalSuppliers(Long totalSuppliers) { this.totalSuppliers = totalSuppliers; return this; }
            public SectionBuilder activeSuppliers(Long activeSuppliers) { this.activeSuppliers = activeSuppliers; return this; }
            public SectionBuilder newSuppliersLast30Days(Long newSuppliersLast30Days) { this.newSuppliersLast30Days = newSuppliersLast30Days; return this; }
            public SectionBuilder overallAverageRating(Double overallAverageRating) { this.overallAverageRating = overallAverageRating; return this; }
            public SectionBuilder topCategory(String topCategory) { this.topCategory = topCategory; return this; }
            public SectionBuilder categoryHealthScore(Double categoryHealthScore) { this.categoryHealthScore = categoryHealthScore; return this; }

            public SupplierOverviewSection build() {
                return new SupplierOverviewSection(totalSuppliers, activeSuppliers, newSuppliersLast30Days, overallAverageRating, topCategory, categoryHealthScore);
            }
        }

        public Long getTotalSuppliers() { return totalSuppliers; }
        public void setTotalSuppliers(Long totalSuppliers) { this.totalSuppliers = totalSuppliers; }
        public Long getActiveSuppliers() { return activeSuppliers; }
        public void setActiveSuppliers(Long activeSuppliers) { this.activeSuppliers = activeSuppliers; }
        public Long getNewSuppliersLast30Days() { return newSuppliersLast30Days; }
        public void setNewSuppliersLast30Days(Long newSuppliersLast30Days) { this.newSuppliersLast30Days = newSuppliersLast30Days; }
        public Double getOverallAverageRating() { return overallAverageRating; }
        public void setOverallAverageRating(Double overallAverageRating) { this.overallAverageRating = overallAverageRating; }
        public String getTopCategory() { return topCategory; }
        public void setTopCategory(String topCategory) { this.topCategory = topCategory; }
        public Double getCategoryHealthScore() { return categoryHealthScore; }
        public void setCategoryHealthScore(Double categoryHealthScore) { this.categoryHealthScore = categoryHealthScore; }
    }

    public static class RiskOverviewSection {
        private Long highRiskSuppliers;
        private Long mediumRiskSuppliers;
        private Long lowRiskSuppliers;
        private Double averageRiskScore;
        private Long criticalAlertsCount;
        private List<String> highRiskVendors = new ArrayList<>();

        public RiskOverviewSection() {}

        public RiskOverviewSection(Long highRiskSuppliers, Long mediumRiskSuppliers, Long lowRiskSuppliers, Double averageRiskScore, Long criticalAlertsCount, List<String> highRiskVendors) {
            this.highRiskSuppliers = highRiskSuppliers;
            this.mediumRiskSuppliers = mediumRiskSuppliers;
            this.lowRiskSuppliers = lowRiskSuppliers;
            this.averageRiskScore = averageRiskScore;
            this.criticalAlertsCount = criticalAlertsCount;
            this.highRiskVendors = highRiskVendors != null ? highRiskVendors : new ArrayList<>();
        }

        public static RiskBuilder builder() {
            return new RiskBuilder();
        }

        public static class RiskBuilder {
            private Long highRiskSuppliers;
            private Long mediumRiskSuppliers;
            private Long lowRiskSuppliers;
            private Double averageRiskScore;
            private Long criticalAlertsCount;
            private List<String> highRiskVendors = new ArrayList<>();

            public RiskBuilder highRiskSuppliers(Long highRiskSuppliers) { this.highRiskSuppliers = highRiskSuppliers; return this; }
            public RiskBuilder mediumRiskSuppliers(Long mediumRiskSuppliers) { this.mediumRiskSuppliers = mediumRiskSuppliers; return this; }
            public RiskBuilder lowRiskSuppliers(Long lowRiskSuppliers) { this.lowRiskSuppliers = lowRiskSuppliers; return this; }
            public RiskBuilder averageRiskScore(Double averageRiskScore) { this.averageRiskScore = averageRiskScore; return this; }
            public RiskBuilder criticalAlertsCount(Long criticalAlertsCount) { this.criticalAlertsCount = criticalAlertsCount; return this; }
            public RiskBuilder highRiskVendors(List<String> highRiskVendors) { this.highRiskVendors = highRiskVendors; return this; }

            public RiskOverviewSection build() {
                return new RiskOverviewSection(highRiskSuppliers, mediumRiskSuppliers, lowRiskSuppliers, averageRiskScore, criticalAlertsCount, highRiskVendors);
            }
        }

        public Long getHighRiskSuppliers() { return highRiskSuppliers; }
        public void setHighRiskSuppliers(Long highRiskSuppliers) { this.highRiskSuppliers = highRiskSuppliers; }
        public Long getMediumRiskSuppliers() { return mediumRiskSuppliers; }
        public void setMediumRiskSuppliers(Long mediumRiskSuppliers) { this.mediumRiskSuppliers = mediumRiskSuppliers; }
        public Long getLowRiskSuppliers() { return lowRiskSuppliers; }
        public void setLowRiskSuppliers(Long lowRiskSuppliers) { this.lowRiskSuppliers = lowRiskSuppliers; }
        public Double getAverageRiskScore() { return averageRiskScore; }
        public void setAverageRiskScore(Double averageRiskScore) { this.averageRiskScore = averageRiskScore; }
        public Long getCriticalAlertsCount() { return criticalAlertsCount; }
        public void setCriticalAlertsCount(Long criticalAlertsCount) { this.criticalAlertsCount = criticalAlertsCount; }
        public List<String> getHighRiskVendors() { return highRiskVendors; }
        public void setHighRiskVendors(List<String> highRiskVendors) { this.highRiskVendors = highRiskVendors; }
    }

    public static class PerformanceOverviewSection {
        private Long improvingSuppliersCount;
        private Long stableSuppliersCount;
        private Long decliningSuppliersCount;
        private Double averageScoreQuarterChange;
        private List<RatingDistributionResponse> ratingDistribution = new ArrayList<>();
        private List<TopSupplierResponse> topSuppliers = new ArrayList<>();
        private List<TopSupplierResponse> bottomSuppliers = new ArrayList<>();

        public PerformanceOverviewSection() {}

        public PerformanceOverviewSection(Long improvingSuppliersCount, Long stableSuppliersCount, Long decliningSuppliersCount, Double averageScoreQuarterChange, List<RatingDistributionResponse> ratingDistribution, List<TopSupplierResponse> topSuppliers, List<TopSupplierResponse> bottomSuppliers) {
            this.improvingSuppliersCount = improvingSuppliersCount;
            this.stableSuppliersCount = stableSuppliersCount;
            this.decliningSuppliersCount = decliningSuppliersCount;
            this.averageScoreQuarterChange = averageScoreQuarterChange;
            this.ratingDistribution = ratingDistribution != null ? ratingDistribution : new ArrayList<>();
            this.topSuppliers = topSuppliers != null ? topSuppliers : new ArrayList<>();
            this.bottomSuppliers = bottomSuppliers != null ? bottomSuppliers : new ArrayList<>();
        }

        public static PerfBuilder builder() {
            return new PerfBuilder();
        }

        public static class PerfBuilder {
            private Long improvingSuppliersCount;
            private Long stableSuppliersCount;
            private Long decliningSuppliersCount;
            private Double averageScoreQuarterChange;
            private List<RatingDistributionResponse> ratingDistribution = new ArrayList<>();
            private List<TopSupplierResponse> topSuppliers = new ArrayList<>();
            private List<TopSupplierResponse> bottomSuppliers = new ArrayList<>();

            public PerfBuilder improvingSuppliersCount(Long improvingSuppliersCount) { this.improvingSuppliersCount = improvingSuppliersCount; return this; }
            public PerfBuilder stableSuppliersCount(Long stableSuppliersCount) { this.stableSuppliersCount = stableSuppliersCount; return this; }
            public PerfBuilder decliningSuppliersCount(Long decliningSuppliersCount) { this.decliningSuppliersCount = decliningSuppliersCount; return this; }
            public PerfBuilder averageScoreQuarterChange(Double averageScoreQuarterChange) { this.averageScoreQuarterChange = averageScoreQuarterChange; return this; }
            public PerfBuilder ratingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; return this; }
            public PerfBuilder topSuppliers(List<TopSupplierResponse> topSuppliers) { this.topSuppliers = topSuppliers; return this; }
            public PerfBuilder bottomSuppliers(List<TopSupplierResponse> bottomSuppliers) { this.bottomSuppliers = bottomSuppliers; return this; }

            public PerformanceOverviewSection build() {
                return new PerformanceOverviewSection(improvingSuppliersCount, stableSuppliersCount, decliningSuppliersCount, averageScoreQuarterChange, ratingDistribution, topSuppliers, bottomSuppliers);
            }
        }

        public Long getImprovingSuppliersCount() { return improvingSuppliersCount; }
        public void setImprovingSuppliersCount(Long improvingSuppliersCount) { this.improvingSuppliersCount = improvingSuppliersCount; }
        public Long getStableSuppliersCount() { return stableSuppliersCount; }
        public void setStableSuppliersCount(Long stableSuppliersCount) { this.stableSuppliersCount = stableSuppliersCount; }
        public Long getDecliningSuppliersCount() { return decliningSuppliersCount; }
        public void setDecliningSuppliersCount(Long decliningSuppliersCount) { this.decliningSuppliersCount = decliningSuppliersCount; }
        public Double getAverageScoreQuarterChange() { return averageScoreQuarterChange; }
        public void setAverageScoreQuarterChange(Double averageScoreQuarterChange) { this.averageScoreQuarterChange = averageScoreQuarterChange; }
        public List<RatingDistributionResponse> getRatingDistribution() { return ratingDistribution; }
        public void setRatingDistribution(List<RatingDistributionResponse> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
        public List<TopSupplierResponse> getTopSuppliers() { return topSuppliers; }
        public void setTopSuppliers(List<TopSupplierResponse> topSuppliers) { this.topSuppliers = topSuppliers; }
        public List<TopSupplierResponse> getBottomSuppliers() { return bottomSuppliers; }
        public void setBottomSuppliers(List<TopSupplierResponse> bottomSuppliers) { this.bottomSuppliers = bottomSuppliers; }
    }

    public static class OperationalOverviewSection {
        private Long openImprovementActions;
        private Long overdueImprovementActions;
        private Double capClosureRate;
        private Long pendingWorkflowApprovals;
        private Long escalatedWorkflows;
        private Double workflowSlaComplianceRate;

        public OperationalOverviewSection() {}

        public OperationalOverviewSection(Long openImprovementActions, Long overdueImprovementActions, Double capClosureRate, Long pendingWorkflowApprovals, Long escalatedWorkflows, Double workflowSlaComplianceRate) {
            this.openImprovementActions = openImprovementActions;
            this.overdueImprovementActions = overdueImprovementActions;
            this.capClosureRate = capClosureRate;
            this.pendingWorkflowApprovals = pendingWorkflowApprovals;
            this.escalatedWorkflows = escalatedWorkflows;
            this.workflowSlaComplianceRate = workflowSlaComplianceRate;
        }

        public static OperBuilder builder() {
            return new OperBuilder();
        }

        public static class OperBuilder {
            private Long openImprovementActions;
            private Long overdueImprovementActions;
            private Double capClosureRate;
            private Long pendingWorkflowApprovals;
            private Long escalatedWorkflows;
            private Double workflowSlaComplianceRate;

            public OperBuilder openImprovementActions(Long openImprovementActions) { this.openImprovementActions = openImprovementActions; return this; }
            public OperBuilder overdueImprovementActions(Long overdueImprovementActions) { this.overdueImprovementActions = overdueImprovementActions; return this; }
            public OperBuilder capClosureRate(Double capClosureRate) { this.capClosureRate = capClosureRate; return this; }
            public OperBuilder pendingWorkflowApprovals(Long pendingWorkflowApprovals) { this.pendingWorkflowApprovals = pendingWorkflowApprovals; return this; }
            public OperBuilder escalatedWorkflows(Long escalatedWorkflows) { this.escalatedWorkflows = escalatedWorkflows; return this; }
            public OperBuilder workflowSlaComplianceRate(Double workflowSlaComplianceRate) { this.workflowSlaComplianceRate = workflowSlaComplianceRate; return this; }

            public OperationalOverviewSection build() {
                return new OperationalOverviewSection(openImprovementActions, overdueImprovementActions, capClosureRate, pendingWorkflowApprovals, escalatedWorkflows, workflowSlaComplianceRate);
            }
        }

        public Long getOpenImprovementActions() { return openImprovementActions; }
        public void setOpenImprovementActions(Long openImprovementActions) { this.openImprovementActions = openImprovementActions; }
        public Long getOverdueImprovementActions() { return overdueImprovementActions; }
        public void setOverdueImprovementActions(Long overdueImprovementActions) { this.overdueImprovementActions = overdueImprovementActions; }
        public Double getCapClosureRate() { return capClosureRate; }
        public void setCapClosureRate(Double capClosureRate) { this.capClosureRate = capClosureRate; }
        public Long getPendingWorkflowApprovals() { return pendingWorkflowApprovals; }
        public void setPendingWorkflowApprovals(Long pendingWorkflowApprovals) { this.pendingWorkflowApprovals = pendingWorkflowApprovals; }
        public Long getEscalatedWorkflows() { return escalatedWorkflows; }
        public void setEscalatedWorkflows(Long escalatedWorkflows) { this.escalatedWorkflows = escalatedWorkflows; }
        public Double getWorkflowSlaComplianceRate() { return workflowSlaComplianceRate; }
        public void setWorkflowSlaComplianceRate(Double workflowSlaComplianceRate) { this.workflowSlaComplianceRate = workflowSlaComplianceRate; }
    }

    public static class StrategicInsightItem {
        private String type;
        private String title;
        private String description;
        private String actionRecommendation;

        public StrategicInsightItem() {}

        public StrategicInsightItem(String type, String title, String description, String actionRecommendation) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.actionRecommendation = actionRecommendation;
        }

        public static InsightBuilder builder() {
            return new InsightBuilder();
        }

        public static class InsightBuilder {
            private String type;
            private String title;
            private String description;
            private String actionRecommendation;

            public InsightBuilder type(String type) { this.type = type; return this; }
            public InsightBuilder title(String title) { this.title = title; return this; }
            public InsightBuilder description(String description) { this.description = description; return this; }
            public InsightBuilder actionRecommendation(String actionRecommendation) { this.actionRecommendation = actionRecommendation; return this; }

            public StrategicInsightItem build() {
                return new StrategicInsightItem(type, title, description, actionRecommendation);
            }
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getActionRecommendation() { return actionRecommendation; }
        public void setActionRecommendation(String actionRecommendation) { this.actionRecommendation = actionRecommendation; }
    }

    public SupplierOverviewSection getSupplierOverview() { return supplierOverview; }
    public void setSupplierOverview(SupplierOverviewSection supplierOverview) { this.supplierOverview = supplierOverview; }
    public RiskOverviewSection getRiskOverview() { return riskOverview; }
    public void setRiskOverview(RiskOverviewSection riskOverview) { this.riskOverview = riskOverview; }
    public PerformanceOverviewSection getPerformanceOverview() { return performanceOverview; }
    public void setPerformanceOverview(PerformanceOverviewSection performanceOverview) { this.performanceOverview = performanceOverview; }
    public OperationalOverviewSection getOperationalOverview() { return operationalOverview; }
    public void setOperationalOverview(OperationalOverviewSection operationalOverview) { this.operationalOverview = operationalOverview; }
    public List<StrategicInsightItem> getStrategicInsights() { return strategicInsights; }
    public void setStrategicInsights(List<StrategicInsightItem> strategicInsights) { this.strategicInsights = strategicInsights; }
}
