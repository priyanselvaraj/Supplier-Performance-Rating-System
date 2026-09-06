package com.supplier.sprsystem.model.entity;

public enum AlertSeverity {
    INFO("Info", "#3b82f6"),
    WARNING("Warning", "#f59e0b"),
    HIGH("High", "#f97316"),
    CRITICAL("Critical", "#ef4444");

    private final String displayName;
    private final String colorCode;

    AlertSeverity(String displayName, String colorCode) {
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
