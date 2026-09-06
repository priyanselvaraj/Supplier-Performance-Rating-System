package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplierPerformanceRatingResponse {

    private Long id;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Long evaluationId;
    private String evaluationCode;
    private Double score;
    private SupplierRating rating;
    private String ratingDisplayName;
    private PerformanceStatus performanceStatus;
    private String performanceStatusDisplayName;
    private LocalDate ratingDate;
    private LocalDateTime createdAt;

    public SupplierPerformanceRatingResponse() {}

    public SupplierPerformanceRatingResponse(Long id, Long supplierId, String supplierCode, String supplierName, Long evaluationId, String evaluationCode, Double score, SupplierRating rating, String ratingDisplayName, PerformanceStatus performanceStatus, String performanceStatusDisplayName, LocalDate ratingDate, LocalDateTime createdAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.evaluationId = evaluationId;
        this.evaluationCode = evaluationCode;
        this.score = score;
        this.rating = rating;
        this.ratingDisplayName = ratingDisplayName;
        this.performanceStatus = performanceStatus;
        this.performanceStatusDisplayName = performanceStatusDisplayName;
        this.ratingDate = ratingDate;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private Long evaluationId;
        private String evaluationCode;
        private Double score;
        private SupplierRating rating;
        private String ratingDisplayName;
        private PerformanceStatus performanceStatus;
        private String performanceStatusDisplayName;
        private LocalDate ratingDate;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder evaluationId(Long evaluationId) { this.evaluationId = evaluationId; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder score(Double score) { this.score = score; return this; }
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
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SupplierPerformanceRatingResponse build() {
            return new SupplierPerformanceRatingResponse(id, supplierId, supplierCode, supplierName, evaluationId, evaluationCode, score, rating, ratingDisplayName, performanceStatus, performanceStatusDisplayName, ratingDate, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
