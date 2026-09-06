package com.supplier.sprsystem.model.entity;

public enum ImprovementActionPriority {
    LOW("Low", "#10b981"),
    MEDIUM("Medium", "#3b82f6"),
    HIGH("High", "#f97316"),
    CRITICAL("Critical", "#ef4444");

    private final String displayName;
    private final String colorCode;

    ImprovementActionPriority(String displayName, String colorCode) {
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
