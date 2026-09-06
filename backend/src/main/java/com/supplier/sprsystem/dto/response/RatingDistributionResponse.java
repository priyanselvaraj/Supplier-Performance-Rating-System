package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.SupplierRating;

public class RatingDistributionResponse {

    private SupplierRating rating;
    private String ratingDisplayName;
    private long count;
    private double percentage;

    public RatingDistributionResponse() {}

    public RatingDistributionResponse(SupplierRating rating, String ratingDisplayName, long count, double percentage) {
        this.rating = rating;
        this.ratingDisplayName = ratingDisplayName != null ? ratingDisplayName : (rating != null ? rating.getDisplayName() : null);
        this.count = count;
        this.percentage = percentage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SupplierRating rating;
        private String ratingDisplayName;
        private long count;
        private double percentage;

        public Builder rating(SupplierRating rating) {
            this.rating = rating;
            if (rating != null) this.ratingDisplayName = rating.getDisplayName();
            return this;
        }
        public Builder ratingDisplayName(String ratingDisplayName) { this.ratingDisplayName = ratingDisplayName; return this; }
        public Builder count(long count) { this.count = count; return this; }
        public Builder percentage(double percentage) { this.percentage = percentage; return this; }

        public RatingDistributionResponse build() {
            return new RatingDistributionResponse(rating, ratingDisplayName, count, percentage);
        }
    }

    public SupplierRating getRating() { return rating; }
    public void setRating(SupplierRating rating) {
        this.rating = rating;
        if (rating != null) this.ratingDisplayName = rating.getDisplayName();
    }
    public String getRatingDisplayName() { return ratingDisplayName; }
    public void setRatingDisplayName(String ratingDisplayName) { this.ratingDisplayName = ratingDisplayName; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
