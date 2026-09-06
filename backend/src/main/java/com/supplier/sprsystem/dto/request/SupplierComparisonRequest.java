package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class SupplierComparisonRequest {

    @NotEmpty(message = "At least two supplier IDs are required for comparison")
    @Size(min = 2, max = 10, message = "Comparison must contain between 2 and 10 suppliers")
    private List<Long> supplierIds = new ArrayList<>();

    private List<String> metrics = new ArrayList<>();

    public SupplierComparisonRequest() {}

    public SupplierComparisonRequest(List<Long> supplierIds, List<String> metrics) {
        this.supplierIds = supplierIds != null ? supplierIds : new ArrayList<>();
        this.metrics = metrics != null ? metrics : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Long> supplierIds = new ArrayList<>();
        private List<String> metrics = new ArrayList<>();

        public Builder supplierIds(List<Long> supplierIds) { this.supplierIds = supplierIds; return this; }
        public Builder metrics(List<String> metrics) { this.metrics = metrics; return this; }

        public SupplierComparisonRequest build() {
            return new SupplierComparisonRequest(supplierIds, metrics);
        }
    }

    public List<Long> getSupplierIds() { return supplierIds; }
    public void setSupplierIds(List<Long> supplierIds) { this.supplierIds = supplierIds; }
    public List<String> getMetrics() { return metrics; }
    public void setMetrics(List<String> metrics) { this.metrics = metrics; }
}
