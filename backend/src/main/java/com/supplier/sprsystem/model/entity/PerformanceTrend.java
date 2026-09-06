package com.supplier.sprsystem.model.entity;

public enum PerformanceTrend {
    IMPROVING("Improving"),
    STABLE("Stable"),
    DECLINING("Declining"),
    INSUFFICIENT_DATA("Insufficient Data");

    private final String displayName;

    PerformanceTrend(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PerformanceTrend calculateTrend(Double currentScore, Double previousScore) {
        if (currentScore == null || previousScore == null) {
            return INSUFFICIENT_DATA;
        }

        double diff = currentScore - previousScore;
        if (diff > 2.0) {
            return IMPROVING;
        } else if (diff < -2.0) {
            return DECLINING;
        } else {
            return STABLE;
        }
    }
}
