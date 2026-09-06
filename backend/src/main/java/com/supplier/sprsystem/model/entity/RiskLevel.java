package com.supplier.sprsystem.model.entity;

public enum RiskLevel {
    LOW("Low Risk", "#10b981"),
    MEDIUM("Medium Risk", "#f59e0b"),
    HIGH("High Risk", "#f97316"),
    CRITICAL("Critical Risk", "#ef4444");

    private final String displayName;
    private final String colorCode;

    RiskLevel(String displayName, String colorCode) {
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
