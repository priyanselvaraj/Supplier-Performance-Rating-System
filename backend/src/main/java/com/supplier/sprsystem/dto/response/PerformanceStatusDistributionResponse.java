package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.PerformanceStatus;

public class PerformanceStatusDistributionResponse {

    private PerformanceStatus performanceStatus;
    private String performanceStatusDisplayName;
    private long count;
    private double percentage;

    public PerformanceStatusDistributionResponse() {}

    public PerformanceStatusDistributionResponse(PerformanceStatus performanceStatus, String performanceStatusDisplayName, long count, double percentage) {
        this.performanceStatus = performanceStatus;
        this.performanceStatusDisplayName = performanceStatusDisplayName != null ? performanceStatusDisplayName : (performanceStatus != null ? performanceStatus.getDisplayName() : null);
        this.count = count;
        this.percentage = percentage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PerformanceStatus performanceStatus;
        private String performanceStatusDisplayName;
        private long count;
        private double percentage;

        public Builder performanceStatus(PerformanceStatus performanceStatus) {
            this.performanceStatus = performanceStatus;
            if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
            return this;
        }
        public Builder performanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; return this; }
        public Builder count(long count) { this.count = count; return this; }
        public Builder percentage(double percentage) { this.percentage = percentage; return this; }

        public PerformanceStatusDistributionResponse build() {
            return new PerformanceStatusDistributionResponse(performanceStatus, performanceStatusDisplayName, count, percentage);
        }
    }

    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) {
        this.performanceStatus = performanceStatus;
        if (performanceStatus != null) this.performanceStatusDisplayName = performanceStatus.getDisplayName();
    }
    public String getPerformanceStatusDisplayName() { return performanceStatusDisplayName; }
    public void setPerformanceStatusDisplayName(String performanceStatusDisplayName) { this.performanceStatusDisplayName = performanceStatusDisplayName; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
