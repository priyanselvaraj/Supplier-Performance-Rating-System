package com.supplier.sprsystem.dto.ai;

import java.time.LocalDateTime;
import java.util.List;

public class ExecutiveAiSummaryResponse {

    private LocalDateTime generatedAt;
    private String executiveSummary;
    private int totalSuppliersEvaluated;
    private Double portfolioAverageScore;
    private int highRiskSuppliersCount;
    private int criticalAlertsCount;
    private int overdueWorkflowsCount;
    private int openImprovementActionsCount;
    private List<String> keyRisks;
    private List<String> performanceHighlights;
    private List<String> areasRequiringAttention;
    private List<StrategicRecommendation> strategicRecommendations;

    public ExecutiveAiSummaryResponse() {}

    public ExecutiveAiSummaryResponse(LocalDateTime generatedAt, String executiveSummary, int totalSuppliersEvaluated, Double portfolioAverageScore, int highRiskSuppliersCount, int criticalAlertsCount, int overdueWorkflowsCount, int openImprovementActionsCount, List<String> keyRisks, List<String> performanceHighlights, List<String> areasRequiringAttention, List<StrategicRecommendation> strategicRecommendations) {
        this.generatedAt = generatedAt;
        this.executiveSummary = executiveSummary;
        this.totalSuppliersEvaluated = totalSuppliersEvaluated;
        this.portfolioAverageScore = portfolioAverageScore;
        this.highRiskSuppliersCount = highRiskSuppliersCount;
        this.criticalAlertsCount = criticalAlertsCount;
        this.overdueWorkflowsCount = overdueWorkflowsCount;
        this.openImprovementActionsCount = openImprovementActionsCount;
        this.keyRisks = keyRisks;
        this.performanceHighlights = performanceHighlights;
        this.areasRequiringAttention = areasRequiringAttention;
        this.strategicRecommendations = strategicRecommendations;
    }

    public static class StrategicRecommendation {
        private String priority;
        private String title;
        private String rationale;
        private String suggestedAction;

        public StrategicRecommendation() {}

        public StrategicRecommendation(String priority, String title, String rationale, String suggestedAction) {
            this.priority = priority;
            this.title = title;
            this.rationale = rationale;
            this.suggestedAction = suggestedAction;
        }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getRationale() { return rationale; }
        public void setRationale(String rationale) { this.rationale = rationale; }
        public String getSuggestedAction() { return suggestedAction; }
        public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
    }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    public int getTotalSuppliersEvaluated() { return totalSuppliersEvaluated; }
    public void setTotalSuppliersEvaluated(int totalSuppliersEvaluated) { this.totalSuppliersEvaluated = totalSuppliersEvaluated; }
    public Double getPortfolioAverageScore() { return portfolioAverageScore; }
    public void setPortfolioAverageScore(Double portfolioAverageScore) { this.portfolioAverageScore = portfolioAverageScore; }
    public int getHighRiskSuppliersCount() { return highRiskSuppliersCount; }
    public void setHighRiskSuppliersCount(int highRiskSuppliersCount) { this.highRiskSuppliersCount = highRiskSuppliersCount; }
    public int getCriticalAlertsCount() { return criticalAlertsCount; }
    public void setCriticalAlertsCount(int criticalAlertsCount) { this.criticalAlertsCount = criticalAlertsCount; }
    public int getOverdueWorkflowsCount() { return overdueWorkflowsCount; }
    public void setOverdueWorkflowsCount(int overdueWorkflowsCount) { this.overdueWorkflowsCount = overdueWorkflowsCount; }
    public int getOpenImprovementActionsCount() { return openImprovementActionsCount; }
    public void setOpenImprovementActionsCount(int openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; }
    public List<String> getKeyRisks() { return keyRisks; }
    public void setKeyRisks(List<String> keyRisks) { this.keyRisks = keyRisks; }
    public List<String> getPerformanceHighlights() { return performanceHighlights; }
    public void setPerformanceHighlights(List<String> performanceHighlights) { this.performanceHighlights = performanceHighlights; }
    public List<String> getAreasRequiringAttention() { return areasRequiringAttention; }
    public void setAreasRequiringAttention(List<String> areasRequiringAttention) { this.areasRequiringAttention = areasRequiringAttention; }
    public List<StrategicRecommendation> getStrategicRecommendations() { return strategicRecommendations; }
    public void setStrategicRecommendations(List<StrategicRecommendation> strategicRecommendations) { this.strategicRecommendations = strategicRecommendations; }
}
