package com.supplier.sprsystem.model.entity;

public enum ConfidenceLevel {
    LOW("Low Confidence"),
    MEDIUM("Medium Confidence"),
    HIGH("High Confidence");

    private final String displayName;

    ConfidenceLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
