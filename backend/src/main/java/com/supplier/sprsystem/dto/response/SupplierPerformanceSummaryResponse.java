package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.PerformanceTrend;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;

public class SupplierPerformanceSummaryResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Double latestScore;
    private SupplierRating latestRating;
    private String latestRatingDisplayName;
    private PerformanceStatus latestPerformanceStatus;
    private String latestPerformanceStatusDisplayName;
    private Double previousScore;
    private SupplierRating previousRating;
    private String previousRatingDisplayName;
    private Double scoreDifference;
    private PerformanceTrend performanceTrend;
    private String performanceTrendDisplayName;
    private LocalDate ratingDate;
    private Integer totalEvaluations;

    public SupplierPerformanceSummaryResponse() {}

    public SupplierPerformanceSummaryResponse(Long supplierId, String supplierCode, String supplierName, Double latestScore, SupplierRating latestRating, String latestRatingDisplayName, PerformanceStatus latestPerformanceStatus, String latestPerformanceStatusDisplayName, Double previousScore, SupplierRating previousRating, String previousRatingDisplayName, Double scoreDifference, PerformanceTrend performanceTrend, String performanceTrendDisplayName, LocalDate ratingDate, Integer totalEvaluations) {
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.latestScore = latestScore;
        this.latestRating = latestRating;
        this.latestRatingDisplayName = latestRatingDisplayName;
        this.latestPerformanceStatus = latestPerformanceStatus;
        this.latestPerformanceStatusDisplayName = latestPerformanceStatusDisplayName;
        this.previousScore = previousScore;
        this.previousRating = previousRating;
        this.previousRatingDisplayName = previousRatingDisplayName;
        this.scoreDifference = scoreDifference;
        this.performanceTrend = performanceTrend;
        this.performanceTrendDisplayName = performanceTrendDisplayName;
        this.ratingDate = ratingDate;
        this.totalEvaluations = totalEvaluations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private Double latestScore;
        private SupplierRating latestRating;
        private String latestRatingDisplayName;
        private PerformanceStatus latestPerformanceStatus;
        private String latestPerformanceStatusDisplayName;
        private Double previousScore;
        private SupplierRating previousRating;
        private String previousRatingDisplayName;
        private Double scoreDifference;
        private PerformanceTrend performanceTrend;
        private String performanceTrendDisplayName;
        private LocalDate ratingDate;
        private Integer totalEvaluations;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder latestScore(Double latestScore) { this.latestScore = latestScore; return this; }
        public Builder latestRating(SupplierRating latestRating) {
            this.latestRating = latestRating;
            if (latestRating != null) this.latestRatingDisplayName = latestRating.getDisplayName();
            return this;
        }
        public Builder latestRatingDisplayName(String latestRatingDisplayName) { this.latestRatingDisplayName = latestRatingDisplayName; return this; }
        public Builder latestPerformanceStatus(PerformanceStatus latestPerformanceStatus) {
            this.latestPerformanceStatus = latestPerformanceStatus;
            if (latestPerformanceStatus != null) this.latestPerformanceStatusDisplayName = latestPerformanceStatus.getDisplayName();
            return this;
        }
        public Builder latestPerformanceStatusDisplayName(String latestPerformanceStatusDisplayName) { this.latestPerformanceStatusDisplayName = latestPerformanceStatusDisplayName; return this; }
        public Builder previousScore(Double previousScore) { this.previousScore = previousScore; return this; }
        public Builder previousRating(SupplierRating previousRating) {
            this.previousRating = previousRating;
            if (previousRating != null) this.previousRatingDisplayName = previousRating.getDisplayName();
            return this;
        }
        public Builder previousRatingDisplayName(String previousRatingDisplayName) { this.previousRatingDisplayName = previousRatingDisplayName; return this; }
        public Builder scoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; return this; }
        public Builder performanceTrend(PerformanceTrend performanceTrend) {
            this.performanceTrend = performanceTrend;
            if (performanceTrend != null) this.performanceTrendDisplayName = performanceTrend.getDisplayName();
            return this;
        }
        public Builder performanceTrendDisplayName(String performanceTrendDisplayName) { this.performanceTrendDisplayName = performanceTrendDisplayName; return this; }
        public Builder ratingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }

        public SupplierPerformanceSummaryResponse build() {
            return new SupplierPerformanceSummaryResponse(supplierId, supplierCode, supplierName, latestScore, latestRating, latestRatingDisplayName, latestPerformanceStatus, latestPerformanceStatusDisplayName, previousScore, previousRating, previousRatingDisplayName, scoreDifference, performanceTrend, performanceTrendDisplayName, ratingDate, totalEvaluations);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Double getLatestScore() { return latestScore; }
    public void setLatestScore(Double latestScore) { this.latestScore = latestScore; }
    public SupplierRating getLatestRating() { return latestRating; }
    public void setLatestRating(SupplierRating latestRating) {
        this.latestRating = latestRating;
        if (latestRating != null) this.latestRatingDisplayName = latestRating.getDisplayName();
    }
    public String getLatestRatingDisplayName() { return latestRatingDisplayName; }
    public void setLatestRatingDisplayName(String latestRatingDisplayName) { this.latestRatingDisplayName = latestRatingDisplayName; }
    public PerformanceStatus getLatestPerformanceStatus() { return latestPerformanceStatus; }
    public void setLatestPerformanceStatus(PerformanceStatus latestPerformanceStatus) {
        this.latestPerformanceStatus = latestPerformanceStatus;
        if (latestPerformanceStatus != null) this.latestPerformanceStatusDisplayName = latestPerformanceStatus.getDisplayName();
    }
    public String getLatestPerformanceStatusDisplayName() { return latestPerformanceStatusDisplayName; }
    public void setLatestPerformanceStatusDisplayName(String latestPerformanceStatusDisplayName) { this.latestPerformanceStatusDisplayName = latestPerformanceStatusDisplayName; }
    public Double getPreviousScore() { return previousScore; }
    public void setPreviousScore(Double previousScore) { this.previousScore = previousScore; }
    public SupplierRating getPreviousRating() { return previousRating; }
    public void setPreviousRating(SupplierRating previousRating) {
        this.previousRating = previousRating;
        if (previousRating != null) this.previousRatingDisplayName = previousRating.getDisplayName();
    }
    public String getPreviousRatingDisplayName() { return previousRatingDisplayName; }
    public void setPreviousRatingDisplayName(String previousRatingDisplayName) { this.previousRatingDisplayName = previousRatingDisplayName; }
    public Double getScoreDifference() { return scoreDifference; }
    public void setScoreDifference(Double scoreDifference) { this.scoreDifference = scoreDifference; }
    public PerformanceTrend getPerformanceTrend() { return performanceTrend; }
    public void setPerformanceTrend(PerformanceTrend performanceTrend) {
        this.performanceTrend = performanceTrend;
        if (performanceTrend != null) this.performanceTrendDisplayName = performanceTrend.getDisplayName();
    }
    public String getPerformanceTrendDisplayName() { return performanceTrendDisplayName; }
    public void setPerformanceTrendDisplayName(String performanceTrendDisplayName) { this.performanceTrendDisplayName = performanceTrendDisplayName; }
    public LocalDate getRatingDate() { return ratingDate; }
    public void setRatingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; }
    public Integer getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
}
