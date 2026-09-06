package com.supplier.sprsystem.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class AiSupplierCompareRequest {

    @NotEmpty(message = "Supplier IDs list cannot be empty")
    @Size(min = 2, max = 5, message = "Comparison requires between 2 and 5 suppliers")
    private List<Long> supplierIds;

    public AiSupplierCompareRequest() {}

    public AiSupplierCompareRequest(List<Long> supplierIds) {
        this.supplierIds = supplierIds;
    }

    public List<Long> getSupplierIds() { return supplierIds; }
    public void setSupplierIds(List<Long> supplierIds) { this.supplierIds = supplierIds; }
}
