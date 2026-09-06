package com.supplier.sprsystem.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class MonitoringSummaryResponse {
    private int totalSuppliers;
    private int activeSuppliers;
    private int highRiskSuppliersCount;
    private int criticalAlertsCount;
    private int openImprovementActionsCount;
    private int inProgressImprovementActionsCount;
    private int completedImprovementActionsCount;
    private int recentEvaluationsCount;
    private int unreadNotificationsCount;
    private String systemHealthStatus;
    private Double averageEvaluationScore;
    private List<AiAlertResponse> criticalAlerts;
    private List<SupplierRiskResponse> topRiskWatchlist;
    private List<ImprovementActionResponse> urgentActions;
    private LocalDateTime timestamp;

    public MonitoringSummaryResponse() {}

    public MonitoringSummaryResponse(int totalSuppliers, int activeSuppliers, int highRiskSuppliersCount, int criticalAlertsCount, int openImprovementActionsCount, int inProgressImprovementActionsCount, int completedImprovementActionsCount, int recentEvaluationsCount, int unreadNotificationsCount, String systemHealthStatus, Double averageEvaluationScore, List<AiAlertResponse> criticalAlerts, List<SupplierRiskResponse> topRiskWatchlist, List<ImprovementActionResponse> urgentActions, LocalDateTime timestamp) {
        this.totalSuppliers = totalSuppliers;
        this.activeSuppliers = activeSuppliers;
        this.highRiskSuppliersCount = highRiskSuppliersCount;
        this.criticalAlertsCount = criticalAlertsCount;
        this.openImprovementActionsCount = openImprovementActionsCount;
        this.inProgressImprovementActionsCount = inProgressImprovementActionsCount;
        this.completedImprovementActionsCount = completedImprovementActionsCount;
        this.recentEvaluationsCount = recentEvaluationsCount;
        this.unreadNotificationsCount = unreadNotificationsCount;
        this.systemHealthStatus = systemHealthStatus;
        this.averageEvaluationScore = averageEvaluationScore;
        this.criticalAlerts = criticalAlerts;
        this.topRiskWatchlist = topRiskWatchlist;
        this.urgentActions = urgentActions;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalSuppliers;
        private int activeSuppliers;
        private int highRiskSuppliersCount;
        private int criticalAlertsCount;
        private int openImprovementActionsCount;
        private int inProgressImprovementActionsCount;
        private int completedImprovementActionsCount;
        private int recentEvaluationsCount;
        private int unreadNotificationsCount;
        private String systemHealthStatus;
        private Double averageEvaluationScore;
        private List<AiAlertResponse> criticalAlerts;
        private List<SupplierRiskResponse> topRiskWatchlist;
        private List<ImprovementActionResponse> urgentActions;
        private LocalDateTime timestamp;

        public Builder totalSuppliers(int count) { this.totalSuppliers = count; return this; }
        public Builder activeSuppliers(int count) { this.activeSuppliers = count; return this; }
        public Builder highRiskSuppliersCount(int count) { this.highRiskSuppliersCount = count; return this; }
        public Builder criticalAlertsCount(int count) { this.criticalAlertsCount = count; return this; }
        public Builder openImprovementActionsCount(int count) { this.openImprovementActionsCount = count; return this; }
        public Builder inProgressImprovementActionsCount(int count) { this.inProgressImprovementActionsCount = count; return this; }
        public Builder completedImprovementActionsCount(int count) { this.completedImprovementActionsCount = count; return this; }
        public Builder recentEvaluationsCount(int count) { this.recentEvaluationsCount = count; return this; }
        public Builder unreadNotificationsCount(int count) { this.unreadNotificationsCount = count; return this; }
        public Builder systemHealthStatus(String status) { this.systemHealthStatus = status; return this; }
        public Builder averageEvaluationScore(Double score) { this.averageEvaluationScore = score; return this; }
        public Builder criticalAlerts(List<AiAlertResponse> alerts) { this.criticalAlerts = alerts; return this; }
        public Builder topRiskWatchlist(List<SupplierRiskResponse> list) { this.topRiskWatchlist = list; return this; }
        public Builder urgentActions(List<ImprovementActionResponse> list) { this.urgentActions = list; return this; }
        public Builder timestamp(LocalDateTime dt) { this.timestamp = dt; return this; }

        public MonitoringSummaryResponse build() {
            return new MonitoringSummaryResponse(totalSuppliers, activeSuppliers, highRiskSuppliersCount, criticalAlertsCount, openImprovementActionsCount, inProgressImprovementActionsCount, completedImprovementActionsCount, recentEvaluationsCount, unreadNotificationsCount, systemHealthStatus, averageEvaluationScore, criticalAlerts, topRiskWatchlist, urgentActions, timestamp);
        }
    }

    public int getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(int totalSuppliers) { this.totalSuppliers = totalSuppliers; }
    public int getActiveSuppliers() { return activeSuppliers; }
    public void setActiveSuppliers(int activeSuppliers) { this.activeSuppliers = activeSuppliers; }
    public int getHighRiskSuppliersCount() { return highRiskSuppliersCount; }
    public void setHighRiskSuppliersCount(int highRiskSuppliersCount) { this.highRiskSuppliersCount = highRiskSuppliersCount; }
    public int getCriticalAlertsCount() { return criticalAlertsCount; }
    public void setCriticalAlertsCount(int criticalAlertsCount) { this.criticalAlertsCount = criticalAlertsCount; }
    public int getOpenImprovementActionsCount() { return openImprovementActionsCount; }
    public void setOpenImprovementActionsCount(int openImprovementActionsCount) { this.openImprovementActionsCount = openImprovementActionsCount; }
    public int getInProgressImprovementActionsCount() { return inProgressImprovementActionsCount; }
    public void setInProgressImprovementActionsCount(int inProgressImprovementActionsCount) { this.inProgressImprovementActionsCount = inProgressImprovementActionsCount; }
    public int getCompletedImprovementActionsCount() { return completedImprovementActionsCount; }
    public void setCompletedImprovementActionsCount(int completedImprovementActionsCount) { this.completedImprovementActionsCount = completedImprovementActionsCount; }
    public int getRecentEvaluationsCount() { return recentEvaluationsCount; }
    public void setRecentEvaluationsCount(int recentEvaluationsCount) { this.recentEvaluationsCount = recentEvaluationsCount; }
    public int getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(int unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }
    public String getSystemHealthStatus() { return systemHealthStatus; }
    public void setSystemHealthStatus(String systemHealthStatus) { this.systemHealthStatus = systemHealthStatus; }
    public Double getAverageEvaluationScore() { return averageEvaluationScore; }
    public void setAverageEvaluationScore(Double averageEvaluationScore) { this.averageEvaluationScore = averageEvaluationScore; }
    public List<AiAlertResponse> getCriticalAlerts() { return criticalAlerts; }
    public void setCriticalAlerts(List<AiAlertResponse> criticalAlerts) { this.criticalAlerts = criticalAlerts; }
    public List<SupplierRiskResponse> getTopRiskWatchlist() { return topRiskWatchlist; }
    public void setTopRiskWatchlist(List<SupplierRiskResponse> topRiskWatchlist) { this.topRiskWatchlist = topRiskWatchlist; }
    public List<ImprovementActionResponse> getUrgentActions() { return urgentActions; }
    public void setUrgentActions(List<ImprovementActionResponse> urgentActions) { this.urgentActions = urgentActions; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
