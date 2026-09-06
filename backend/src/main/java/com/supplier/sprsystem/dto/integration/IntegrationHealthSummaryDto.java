package com.supplier.sprsystem.dto.integration;

import java.time.LocalDateTime;

public class IntegrationHealthSummaryDto {

    private long activeApiKeysCount;
    private long totalApiKeysCount;
    private long activeWebhooksCount;
    private long totalWebhooksCount;
    private long totalDeliveriesCount;
    private long successfulDeliveriesCount;
    private long failedDeliveriesCount;
    private double deliverySuccessRate;
    private long totalSyncRunsCount;
    private long successfulSyncRunsCount;
    private double syncSuccessRate;
    private LocalDateTime lastSyncTime;
    private LocalDateTime lastWebhookDeliveryTime;

    public IntegrationHealthSummaryDto() {
    }

    public IntegrationHealthSummaryDto(long activeApiKeysCount, long totalApiKeysCount,
                                       long activeWebhooksCount, long totalWebhooksCount,
                                       long totalDeliveriesCount, long successfulDeliveriesCount,
                                       long failedDeliveriesCount, double deliverySuccessRate,
                                       long totalSyncRunsCount, long successfulSyncRunsCount,
                                       double syncSuccessRate, LocalDateTime lastSyncTime,
                                       LocalDateTime lastWebhookDeliveryTime) {
        this.activeApiKeysCount = activeApiKeysCount;
        this.totalApiKeysCount = totalApiKeysCount;
        this.activeWebhooksCount = activeWebhooksCount;
        this.totalWebhooksCount = totalWebhooksCount;
        this.totalDeliveriesCount = totalDeliveriesCount;
        this.successfulDeliveriesCount = successfulDeliveriesCount;
        this.failedDeliveriesCount = failedDeliveriesCount;
        this.deliverySuccessRate = deliverySuccessRate;
        this.totalSyncRunsCount = totalSyncRunsCount;
        this.successfulSyncRunsCount = successfulSyncRunsCount;
        this.syncSuccessRate = syncSuccessRate;
        this.lastSyncTime = lastSyncTime;
        this.lastWebhookDeliveryTime = lastWebhookDeliveryTime;
    }

    // Getters and Setters
    public long getActiveApiKeysCount() {
        return activeApiKeysCount;
    }

    public void setActiveApiKeysCount(long activeApiKeysCount) {
        this.activeApiKeysCount = activeApiKeysCount;
    }

    public long getTotalApiKeysCount() {
        return totalApiKeysCount;
    }

    public void setTotalApiKeysCount(long totalApiKeysCount) {
        this.totalApiKeysCount = totalApiKeysCount;
    }

    public long getActiveWebhooksCount() {
        return activeWebhooksCount;
    }

    public void setActiveWebhooksCount(long activeWebhooksCount) {
        this.activeWebhooksCount = activeWebhooksCount;
    }

    public long getTotalWebhooksCount() {
        return totalWebhooksCount;
    }

    public void setTotalWebhooksCount(long totalWebhooksCount) {
        this.totalWebhooksCount = totalWebhooksCount;
    }

    public long getTotalDeliveriesCount() {
        return totalDeliveriesCount;
    }

    public void setTotalDeliveriesCount(long totalDeliveriesCount) {
        this.totalDeliveriesCount = totalDeliveriesCount;
    }

    public long getSuccessfulDeliveriesCount() {
        return successfulDeliveriesCount;
    }

    public void setSuccessfulDeliveriesCount(long successfulDeliveriesCount) {
        this.successfulDeliveriesCount = successfulDeliveriesCount;
    }

    public long getFailedDeliveriesCount() {
        return failedDeliveriesCount;
    }

    public void setFailedDeliveriesCount(long failedDeliveriesCount) {
        this.failedDeliveriesCount = failedDeliveriesCount;
    }

    public double getDeliverySuccessRate() {
        return deliverySuccessRate;
    }

    public void setDeliverySuccessRate(double deliverySuccessRate) {
        this.deliverySuccessRate = deliverySuccessRate;
    }

    public long getTotalSyncRunsCount() {
        return totalSyncRunsCount;
    }

    public void setTotalSyncRunsCount(long totalSyncRunsCount) {
        this.totalSyncRunsCount = totalSyncRunsCount;
    }

    public long getSuccessfulSyncRunsCount() {
        return successfulSyncRunsCount;
    }

    public void setSuccessfulSyncRunsCount(long successfulSyncRunsCount) {
        this.successfulSyncRunsCount = successfulSyncRunsCount;
    }

    public double getSyncSuccessRate() {
        return syncSuccessRate;
    }

    public void setSyncSuccessRate(double syncSuccessRate) {
        this.syncSuccessRate = syncSuccessRate;
    }

    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public LocalDateTime getLastWebhookDeliveryTime() {
        return lastWebhookDeliveryTime;
    }

    public void setLastWebhookDeliveryTime(LocalDateTime lastWebhookDeliveryTime) {
        this.lastWebhookDeliveryTime = lastWebhookDeliveryTime;
    }
}
