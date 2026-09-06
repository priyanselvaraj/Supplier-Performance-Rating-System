package com.supplier.sprsystem.dto.response;

import java.util.List;

public class RatingHistoryResponse {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private List<SupplierPerformanceRatingResponse> ratings;

    public RatingHistoryResponse() {}

    public RatingHistoryResponse(Long supplierId, String supplierCode, String supplierName, List<SupplierPerformanceRatingResponse> ratings) {
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.ratings = ratings;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private List<SupplierPerformanceRatingResponse> ratings;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder ratings(List<SupplierPerformanceRatingResponse> ratings) { this.ratings = ratings; return this; }

        public RatingHistoryResponse build() {
            return new RatingHistoryResponse(supplierId, supplierCode, supplierName, ratings);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public List<SupplierPerformanceRatingResponse> getRatings() { return ratings; }
    public void setRatings(List<SupplierPerformanceRatingResponse> ratings) { this.ratings = ratings; }
}
