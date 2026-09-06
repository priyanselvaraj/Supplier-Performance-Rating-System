package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;
import com.supplier.sprsystem.model.entity.SupplierRating;

import java.time.LocalDate;

public class PerformanceTrendResponse {

    private LocalDate date;
    private Double score;
    private SupplierRating rating;
    private String ratingDisplayName;
    private PerformanceStatus performanceStatus;
    private String performanceStatusDisplayName;

    public PerformanceTrendResponse() {}

    public PerformanceTrendResponse(LocalDate date, Double score, SupplierRating rating, String ratingDisplayName, PerformanceStatus performanceStatus, String performanceStatusDisplayName) {
        this.date = date;
        this.score = score;
        this.rating = rating;
        this.ratingDisplayName = ratingDisplayName != null ? ratingDisplayName : (rating != null ? rating.getDisplayName() : null);
        this.performanceStatus = performanceStatus;
        this.performanceStatusDisplayName = performanceStatusDisplayName != null ? performanceStatusDisplayName : (performanceStatus != null ? performanceStatus.getDisplayName() : null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate date;
        private Double score;
        private SupplierRating rating;
        private String ratingDisplayName;
        private PerformanceStatus performanceStatus;
        private String performanceStatusDisplayName;

        public Builder date(LocalDate date) { this.date = date; return this; }
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

        public PerformanceTrendResponse build() {
            return new PerformanceTrendResponse(date, score, rating, ratingDisplayName, performanceStatus, performanceStatusDisplayName);
        }
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
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
}
