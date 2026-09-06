package com.supplier.sprsystem.model.entity;

public enum ImprovementActionStatus {
    OPEN("Open", "#3b82f6"),
    IN_PROGRESS("In Progress", "#f59e0b"),
    COMPLETED("Completed", "#10b981"),
    CANCELLED("Cancelled", "#6b7280");

    private final String displayName;
    private final String colorCode;

    ImprovementActionStatus(String displayName, String colorCode) {
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
