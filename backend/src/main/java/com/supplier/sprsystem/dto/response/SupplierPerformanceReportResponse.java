package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;
import java.util.List;

public class SupplierPerformanceReportResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String categoryName;
    private boolean active;
    private Double latestScore;
    private SupplierRating latestRating;
    private String latestRatingDisplayName;
    private PerformanceStatus performanceStatus;
    private String performanceStatusDisplayName;
    private Double previousScore;
    private Double scoreDifference;
    private PerformanceTrend performanceTrend;
    private String performanceTrendDisplayName;
    private LocalDate latestRatingDate;
    private List<SupplierPerformanceRatingResponse> ratingHistory;

    public SupplierPerformanceReportResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String categoryName;
        private boolean active;
        private Double latestScore;
        private SupplierRating latestRating;
        private String latestRatingDisplayName;
        private PerformanceStatus performanceStatus;
        private String performanceStatusDisplayName;
        private Double previousScore;
        private Double scoreDifference;
        private PerformanceTrend performanceTrend;
        private String performanceTrendDisplayName;
        private LocalDate latestRatingDate;
        private List<SupplierPerformanceRatingResponse> ratingHistory;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder latestScore(Double latestScore) { this.latestScore = latestScore; return this; }
        public Builder latestRating(SupplierRating latestRating) {
            this.latestRating = latestRating;
            if (latestRating != null) this.latestRatingDisplayName = latestRating.getDisplayName();
            return this;
        }
        public Builder latestRatingDisplayName(String latestRatingDisplayName) { this.latestRatingDisplayName = latestRatingDisplayName; return this; }
        public Builder performanceStatus(PerformanceStatus performanceStatus) {
            this.performanceStatus = performanceStatus;
            if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
            return this;
        }
        public Builder performanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; return this; }
        public Builder previousScore(Double previousScore) { this.previousScore = previousScore; return this; }
        public Builder scoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; return this; }
        public Builder performanceTrend(PerformanceTrend performanceTrend) {
            this.performanceTrend = performanceTrend;
            if (performanceTrend != null) this.performanceTrendDisplayName = performanceTrend.getDisplayName();
            return this;
        }
        public Builder performanceTrendDisplayName(String performanceTrendDisplayName) { this.performanceTrendDisplayName = performanceTrendDisplayName; return this; }
        public Builder latestRatingDate(LocalDate latestRatingDate) { this.latestRatingDate = latestRatingDate; return this; }
        public Builder ratingHistory(List<SupplierPerformanceRatingResponse> ratingHistory) { this.ratingHistory = ratingHistory; return this; }

        public SupplierPerformanceReportResponse build() {
            SupplierPerformanceReportResponse r = new SupplierPerformanceReportResponse();
            r.supplierId = this.supplierId;
            r.supplierCode = this.supplierCode;
            r.supplierName = this.supplierName;
            r.categoryName = this.categoryName;
            r.active = this.active;
            r.latestScore = this.latestScore;
            r.latestRating = this.latestRating;
            r.latestRatingDisplayName = this.latestRatingDisplayName;
            r.performanceStatus = this.performanceStatus;
            r.performanceStatusDisplayName = this.performanceStatusDisplayName;
            r.previousScore = this.previousScore;
            r.scoreDifference = this.scoreDifference;
            r.performanceTrend = this.performanceTrend;
            r.performanceTrendDisplayName = this.performanceTrendDisplayName;
            r.latestRatingDate = this.latestRatingDate;
            r.ratingHistory = this.ratingHistory;
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
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Double getLatestScore() { return latestScore; }
    public void setLatestScore(Double latestScore) { this.latestScore = latestScore; }
    public SupplierRating getLatestRating() { return latestRating; }
    public void setLatestRating(SupplierRating latestRating) {
        this.latestRating = latestRating;
        if (latestRating != null) this.latestRatingDisplayName = latestRating.getDisplayName();
    }
    public String getLatestRatingDisplayName() { return latestRatingDisplayName; }
    public void setLatestRatingDisplayName(String latestRatingDisplayName) { this.latestRatingDisplayName = latestRatingDisplayName; }
    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) {
        this.performanceStatus = performanceStatus;
        if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
    }
    public String getPerformanceStatusDisplayName() { return performanceStatusDisplayName; }
    public void setPerformanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; }
    public Double getPreviousScore() { return previousScore; }
    public void setPreviousScore(Double previousScore) { this.previousScore = previousScore; }
    public Double getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; }
    public PerformanceTrend getPerformanceTrend() { return performanceTrend; }
    public void setPerformanceTrend(PerformanceTrend performanceTrend) {
        this.performanceTrend = performanceTrend;
        if (performanceTrend != null) this.performanceTrendDisplayName = performanceTrend.getDisplayName();
    }
    public String getPerformanceTrendDisplayName() { return performanceTrendDisplayName; }
    public void setPerformanceTrendDisplayName(String performanceTrendDisplayName) { this.performanceTrendDisplayName = performanceTrendDisplayName; }
    public LocalDate getLatestRatingDate() { return latestRatingDate; }
    public void setLatestRatingDate(LocalDate latestRatingDate) { this.latestRatingDate = latestRatingDate; }
    public List<SupplierPerformanceRatingResponse> getRatingHistory() { return ratingHistory; }
    public void setRatingHistory(List<SupplierPerformanceRatingResponse> ratingHistory) { this.ratingHistory = ratingHistory; }
}
