package com.supplier.sprsystem.dto.response;

import java.util.List;

public class SupplierStatisticsResponse {

    private long totalSuppliers;
    private long activeSuppliers;
    private long inactiveSuppliers;
    private List<CategorySupplierStatisticsResponse> suppliersByCategory;

    public SupplierStatisticsResponse() {}

    public SupplierStatisticsResponse(long totalSuppliers, long activeSuppliers, long inactiveSuppliers, List<CategorySupplierStatisticsResponse> suppliersByCategory) {
        this.totalSuppliers = totalSuppliers;
        this.activeSuppliers = activeSuppliers;
        this.inactiveSuppliers = inactiveSuppliers;
        this.suppliersByCategory = suppliersByCategory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalSuppliers;
        private long activeSuppliers;
        private long inactiveSuppliers;
        private List<CategorySupplierStatisticsResponse> suppliersByCategory;

        public Builder totalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; return this; }
        public Builder activeSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; return this; }
        public Builder inactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; return this; }
        public Builder suppliersByCategory(List<CategorySupplierStatisticsResponse> suppliersByCategory) { this.suppliersByCategory = suppliersByCategory; return this; }

        public SupplierStatisticsResponse build() {
            return new SupplierStatisticsResponse(totalSuppliers, activeSuppliers, inactiveSuppliers, suppliersByCategory);
        }
    }

    public long getTotalSuppliers() { return totalSuppliers; }
    public void setTotalSuppliers(long totalSuppliers) { this.totalSuppliers = totalSuppliers; }
    public long getActiveSuppliers() { return activeSuppliers; }
    public void setActiveSuppliers(long activeSuppliers) { this.activeSuppliers = activeSuppliers; }
    public long getInactiveSuppliers() { return inactiveSuppliers; }
    public void setInactiveSuppliers(long inactiveSuppliers) { this.inactiveSuppliers = inactiveSuppliers; }
    public List<CategorySupplierStatisticsResponse> getSuppliersByCategory() { return suppliersByCategory; }
    public void setSuppliersByCategory(List<CategorySupplierStatisticsResponse> suppliersByCategory) { this.suppliersByCategory = suppliersByCategory; }
}
