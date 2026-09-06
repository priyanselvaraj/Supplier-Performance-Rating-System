package com.supplier.sprsystem.model.entity;

public enum PerformanceStatus {
    HIGH_PERFORMING("High Performing"),
    SATISFACTORY("Satisfactory"),
    NEEDS_IMPROVEMENT("Needs Improvement"),
    LOW_PERFORMING("Low Performing");

    private final String displayName;

    PerformanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PerformanceStatus fromRating(SupplierRating rating) {
        switch (rating) {
            case EXCELLENT:
            case VERY_GOOD:
                return HIGH_PERFORMING;
            case GOOD:
                return SATISFACTORY;
            case AVERAGE:
                return NEEDS_IMPROVEMENT;
            case POOR:
            default:
                return LOW_PERFORMING;
        }
    }

    public static PerformanceStatus fromScore(double score) {
        return fromRating(SupplierRating.fromScore(score));
    }
}
