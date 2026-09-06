package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_sync_histories",
        indexes = {
                @Index(name = "idx_sync_status", columnList = "status"),
                @Index(name = "idx_sync_started_at", columnList = "started_at"),
                @Index(name = "idx_sync_type", columnList = "integration_type")
        })
public class IntegrationSyncHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @Column(name = "source_system", length = 100)
    private String sourceSystem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "integration_type", nullable = false, length = 50)
    private IntegrationType integrationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_mode", nullable = false, length = 50)
    private SyncMode syncMode;

    @Column(name = "total_records", nullable = false)
    private int totalRecords = 0;

    @Column(name = "created_count", nullable = false)
    private int createdCount = 0;

    @Column(name = "updated_count", nullable = false)
    private int updatedCount = 0;

    @Column(name = "skipped_count", nullable = false)
    private int skippedCount = 0;

    @Column(name = "failed_count", nullable = false)
    private int failedCount = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SyncStatus status = SyncStatus.IN_PROGRESS;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Size(max = 100)
    @Column(name = "executed_by_name", length = 100)
    private String executedByName;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    public IntegrationSyncHistory() {
    }

    public IntegrationSyncHistory(String sourceSystem, IntegrationType integrationType, SyncMode syncMode, String executedByName) {
        this.sourceSystem = sourceSystem;
        this.integrationType = integrationType;
        this.syncMode = syncMode;
        this.executedByName = executedByName;
        this.status = SyncStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }

    public void markCompleted(SyncStatus status, String errorMessage, String detailsJson) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.detailsJson = detailsJson;
        this.completedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.durationMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public SyncStatus getStatus() {
        return status;
    }

    public void setStatus(SyncStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    public String getExecutedByName() {
        return executedByName;
    }

    public void setExecutedByName(String executedByName) {
        this.executedByName = executedByName;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
