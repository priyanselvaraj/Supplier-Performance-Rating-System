package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;

import java.time.LocalDate;
import java.util.List;

public class SupplierPortalDashboardResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String categoryName;
    private SupplierStatus status;
    private String contactPerson;
    private String email;
    private String phone;

    private Double overallRating;
    private RatingCategory ratingCategory;
    private PerformanceStatus performanceStatus;
    private Integer totalEvaluations;
    private LocalDate lastEvaluationDate;
    private Double scoreDifference;
    private PerformanceTrend performanceTrend;

    private long openActionsCount;
    private long inProgressActionsCount;
    private long completedActionsCount;
    private long totalDocumentsCount;
    private long unreadNotificationsCount;

    private List<SupplierPortalEvaluationResponse> recentEvaluations;
    private List<ImprovementActionResponse> urgentActions;
    private SupplierAiInsightsResponse aiInsights;

    public SupplierPortalDashboardResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String categoryName;
        private SupplierStatus status;
        private String contactPerson;
        private String email;
        private String phone;

        private Double overallRating;
        private RatingCategory ratingCategory;
        private PerformanceStatus performanceStatus;
        private Integer totalEvaluations;
        private LocalDate lastEvaluationDate;
        private Double scoreDifference;
        private PerformanceTrend performanceTrend;

        private long openActionsCount;
        private long inProgressActionsCount;
        private long completedActionsCount;
        private long totalDocumentsCount;
        private long unreadNotificationsCount;

        private List<SupplierPortalEvaluationResponse> recentEvaluations;
        private List<ImprovementActionResponse> urgentActions;
        private SupplierAiInsightsResponse aiInsights;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder status(SupplierStatus status) { this.status = status; return this; }
        public Builder contactPerson(String contactPerson) { this.contactPerson = contactPerson; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder overallRating(Double overallRating) { this.overallRating = overallRating; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder performanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder lastEvaluationDate(LocalDate lastEvaluationDate) { this.lastEvaluationDate = lastEvaluationDate; return this; }
        public Builder scoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; return this; }
        public Builder performanceTrend(PerformanceTrend performanceTrend) { this.performanceTrend = performanceTrend; return this; }
        public Builder openActionsCount(long openActionsCount) { this.openActionsCount = openActionsCount; return this; }
        public Builder inProgressActionsCount(long inProgressActionsCount) { this.inProgressActionsCount = inProgressActionsCount; return this; }
        public Builder completedActionsCount(long completedActionsCount) { this.completedActionsCount = completedActionsCount; return this; }
        public Builder totalDocumentsCount(long totalDocumentsCount) { this.totalDocumentsCount = totalDocumentsCount; return this; }
        public Builder unreadNotificationsCount(long unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; return this; }
        public Builder recentEvaluations(List<SupplierPortalEvaluationResponse> recentEvaluations) { this.recentEvaluations = recentEvaluations; return this; }
        public Builder urgentActions(List<ImprovementActionResponse> urgentActions) { this.urgentActions = urgentActions; return this; }
        public Builder aiInsights(SupplierAiInsightsResponse aiInsights) { this.aiInsights = aiInsights; return this; }

        public SupplierPortalDashboardResponse build() {
            SupplierPortalDashboardResponse r = new SupplierPortalDashboardResponse();
            r.supplierId = this.supplierId;
            r.supplierCode = this.supplierCode;
            r.supplierName = this.supplierName;
            r.categoryName = this.categoryName;
            r.status = this.status;
            r.contactPerson = this.contactPerson;
            r.email = this.email;
            r.phone = this.phone;
            r.overallRating = this.overallRating;
            r.ratingCategory = this.ratingCategory;
            r.performanceStatus = this.performanceStatus;
            r.totalEvaluations = this.totalEvaluations;
            r.lastEvaluationDate = this.lastEvaluationDate;
            r.scoreDifference = this.scoreDifference;
            r.performanceTrend = this.performanceTrend;
            r.openActionsCount = this.openActionsCount;
            r.inProgressActionsCount = this.inProgressActionsCount;
            r.completedActionsCount = this.completedActionsCount;
            r.totalDocumentsCount = this.totalDocumentsCount;
            r.unreadNotificationsCount = this.unreadNotificationsCount;
            r.recentEvaluations = this.recentEvaluations;
            r.urgentActions = this.urgentActions;
            r.aiInsights = this.aiInsights;
            return r;
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public SupplierStatus getStatus() { return status; }
    public void setStatus(SupplierStatus status) { this.status = status; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getOverallRating() { return overallRating; }
    public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; }
    public Integer getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public LocalDate getLastEvaluationDate() { return lastEvaluationDate; }
    public void setLastEvaluationDate(LocalDate lastEvaluationDate) { this.lastEvaluationDate = lastEvaluationDate; }
    public Double getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; }
    public PerformanceTrend getPerformanceTrend() { return performanceTrend; }
    public void setPerformanceTrend(PerformanceTrend performanceTrend) { this.performanceTrend = performanceTrend; }
    public long getOpenActionsCount() { return openActionsCount; }
    public void setOpenActionsCount(long openActionsCount) { this.openActionsCount = openActionsCount; }
    public long getInProgressActionsCount() { return inProgressActionsCount; }
    public void setInProgressActionsCount(long inProgressActionsCount) { this.inProgressActionsCount = inProgressActionsCount; }
    public long getCompletedActionsCount() { return completedActionsCount; }
    public void setCompletedActionsCount(long completedActionsCount) { this.completedActionsCount = completedActionsCount; }
    public long getTotalDocumentsCount() { return totalDocumentsCount; }
    public void setTotalDocumentsCount(long totalDocumentsCount) { this.totalDocumentsCount = totalDocumentsCount; }
    public long getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(long unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }
    public List<SupplierPortalEvaluationResponse> getRecentEvaluations() { return recentEvaluations; }
    public void setRecentEvaluations(List<SupplierPortalEvaluationResponse> recentEvaluations) { this.recentEvaluations = recentEvaluations; }
    public List<ImprovementActionResponse> getUrgentActions() { return urgentActions; }
    public void setUrgentActions(List<ImprovementActionResponse> urgentActions) { this.urgentActions = urgentActions; }
    public SupplierAiInsightsResponse getAiInsights() { return aiInsights; }
    public void setAiInsights(SupplierAiInsightsResponse aiInsights) { this.aiInsights = aiInsights; }
}
