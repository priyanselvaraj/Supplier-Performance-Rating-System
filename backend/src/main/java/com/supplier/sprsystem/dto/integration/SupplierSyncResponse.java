package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.SyncMode;
import com.supplier.sprsystem.model.entity.SyncStatus;

import java.util.ArrayList;
import java.util.List;

public class SupplierSyncResponse {

    private Long syncHistoryId;
    private String sourceSystem;
    private SyncMode syncMode;
    private SyncStatus status;
    private int totalRecords;
    private int createdCount;
    private int updatedCount;
    private int skippedCount;
    private int failedCount;
    private Long durationMs;
    private List<String> errors = new ArrayList<>();
    private String message;

    public SupplierSyncResponse() {
    }

    public SupplierSyncResponse(Long syncHistoryId, String sourceSystem, SyncMode syncMode,
                                SyncStatus status, int totalRecords, int createdCount,
                                int updatedCount, int skippedCount, int failedCount,
                                Long durationMs, List<String> errors, String message) {
        this.syncHistoryId = syncHistoryId;
        this.sourceSystem = sourceSystem;
        this.syncMode = syncMode;
        this.status = status;
        this.totalRecords = totalRecords;
        this.createdCount = createdCount;
        this.updatedCount = updatedCount;
        this.skippedCount = skippedCount;
        this.failedCount = failedCount;
        this.durationMs = durationMs;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.message = message;
    }

    // Getters and Setters
    public Long getSyncHistoryId() {
        return syncHistoryId;
    }

    public void setSyncHistoryId(Long syncHistoryId) {
        this.syncHistoryId = syncHistoryId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public SyncStatus getStatus() {
        return status;
    }

    public void setStatus(SyncStatus status) {
        this.status = status;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
