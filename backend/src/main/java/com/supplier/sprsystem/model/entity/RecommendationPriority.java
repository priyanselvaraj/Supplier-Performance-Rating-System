package com.supplier.sprsystem.model.entity;

public enum RecommendationPriority {
    LOW("Low Priority", "#10b981"),
    MEDIUM("Medium Priority", "#3b82f6"),
    HIGH("High Priority", "#f59e0b"),
    CRITICAL("Critical Priority", "#ef4444");

    private final String displayName;
    private final String colorCode;

    RecommendationPriority(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
