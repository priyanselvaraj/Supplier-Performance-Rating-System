package com.supplier.sprsystem.model.entity;

public enum SupplierRating {
    EXCELLENT("Excellent", 90.0, 100.0),
    VERY_GOOD("Very Good", 80.0, 89.99),
    GOOD("Good", 70.0, 79.99),
    AVERAGE("Average", 60.0, 69.99),
    POOR("Poor", 0.0, 59.99);

    private final String displayName;
    private final double minScore;
    private final double maxScore;

    SupplierRating(String displayName, double minScore, double maxScore) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMinScore() {
        return minScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public static SupplierRating fromScore(double score) {
        if (score >= 90.0) {
            return EXCELLENT;
        } else if (score >= 80.0) {
            return VERY_GOOD;
        } else if (score >= 70.0) {
            return GOOD;
        } else if (score >= 60.0) {
            return AVERAGE;
        } else {
            return POOR;
        }
    }
}
