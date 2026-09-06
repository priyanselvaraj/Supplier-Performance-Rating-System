package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.IntegrationType;
import com.supplier.sprsystem.model.entity.SyncMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class SupplierSyncRequest {

    @Size(max = 100)
    private String sourceSystem = "External ERP";

    private IntegrationType integrationType = IntegrationType.SUPPLIER_CATALOG;

    @NotNull(message = "Sync mode is required")
    private SyncMode syncMode = SyncMode.UPSERT;

    @NotEmpty(message = "Suppliers list cannot be empty")
    @Valid
    private List<SupplierSyncItem> suppliers;

    public SupplierSyncRequest() {
    }

    public SupplierSyncRequest(String sourceSystem, IntegrationType integrationType, SyncMode syncMode, List<SupplierSyncItem> suppliers) {
        this.sourceSystem = sourceSystem;
        this.integrationType = integrationType != null ? integrationType : IntegrationType.SUPPLIER_CATALOG;
        this.syncMode = syncMode != null ? syncMode : SyncMode.UPSERT;
        this.suppliers = suppliers;
    }

    // Getters and Setters
    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public IntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(IntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public List<SupplierSyncItem> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<SupplierSyncItem> suppliers) {
        this.suppliers = suppliers;
    }
}
