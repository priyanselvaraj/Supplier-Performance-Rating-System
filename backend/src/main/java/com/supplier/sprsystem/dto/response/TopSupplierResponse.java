package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;

public class TopSupplierResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String categoryName;
    private Double latestScore;
    private SupplierRating rating;
    private String ratingDisplayName;
    private PerformanceStatus performanceStatus;
    private String performanceStatusDisplayName;
    private LocalDate ratingDate;

    public TopSupplierResponse() {}

    public TopSupplierResponse(Long supplierId, String supplierCode, String supplierName, String categoryName, Double latestScore, SupplierRating rating, String ratingDisplayName, PerformanceStatus performanceStatus, String performanceStatusDisplayName, LocalDate ratingDate) {
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.categoryName = categoryName;
        this.latestScore = latestScore;
        this.rating = rating;
        this.ratingDisplayName = ratingDisplayName != null ? ratingDisplayName : (rating != null ? rating.getDisplayName() : null);
        this.performanceStatus = performanceStatus;
        this.performanceStatusDisplayName = performanceStatusDisplayName != null ? performanceStatusDisplayName : (performanceStatus != null ? performanceStatus.getDisplayName() : null);
        this.ratingDate = ratingDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String categoryName;
        private Double latestScore;
        private SupplierRating rating;
        private String ratingDisplayName;
        private PerformanceStatus performanceStatus;
        private String performanceStatusDisplayName;
        private LocalDate ratingDate;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder latestScore(Double latestScore) { this.latestScore = latestScore; return this; }
        public Builder rating(SupplierRating rating) {
            this.rating = rating;
            if (rating != null) this.ratingDisplayName = rating.getDisplayName();
            return this;
        }
        public Builder ratingDisplayName(String ratingDisplayName) { this.ratingDisplayName = ratingDisplayName; return this; }
        public Builder performanceStatus(PerformanceStatus performanceStatus) {
            this.performanceStatus = performanceStatus;
            if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
            return this;
        }
        public Builder performanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; return this; }
        public Builder ratingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; return this; }

        public TopSupplierResponse build() {
            return new TopSupplierResponse(supplierId, supplierCode, supplierName, categoryName, latestScore, rating, ratingDisplayName, performanceStatus, performanceStatusDisplayName, ratingDate);
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
    public Double getLatestScore() { return latestScore; }
    public void setLatestScore(Double latestScore) { this.latestScore = latestScore; }
    public SupplierRating getRating() { return rating; }
    public void setRating(SupplierRating rating) {
        this.rating = rating;
        if (rating != null) this.ratingDisplayName = rating.getDisplayName();
    }
    public String getRatingDisplayName() { return ratingDisplayName; }
    public void setRatingDisplayName(String ratingDisplayName) { this.ratingDisplayName = ratingDisplayName; }
    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) {
        this.performanceStatus = performanceStatus;
        if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
    }
    public String getPerformanceStatusDisplayName() { return performanceStatusDisplayName; }
    public void setPerformanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; }
    public LocalDate getRatingDate() { return ratingDate; }
    public void setRatingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; }
}
