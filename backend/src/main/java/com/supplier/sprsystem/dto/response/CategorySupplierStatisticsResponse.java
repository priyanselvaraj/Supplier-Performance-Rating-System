package com.supplier.sprsystem.dto.response;

public class CategorySupplierStatisticsResponse {

    private Long categoryId;
    private String categoryName;
    private long supplierCount;

    public CategorySupplierStatisticsResponse() {}

    public CategorySupplierStatisticsResponse(Long categoryId, String categoryName, long supplierCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.supplierCount = supplierCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long categoryId;
        private String categoryName;
        private long supplierCount;

        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder supplierCount(long supplierCount) { this.supplierCount = supplierCount; return this; }

        public CategorySupplierStatisticsResponse build() {
            return new CategorySupplierStatisticsResponse(categoryId, categoryName, supplierCount);
        }
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public long getSupplierCount() { return supplierCount; }
    public void setSupplierCount(long supplierCount) { this.supplierCount = supplierCount; }
}
