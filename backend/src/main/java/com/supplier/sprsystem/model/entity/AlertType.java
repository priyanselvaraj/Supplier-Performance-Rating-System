package com.supplier.sprsystem.model.entity;

public enum AlertType {
    PERFORMANCE_DECLINE("Performance Decline"),
    LOW_PERFORMANCE("Low Performance"),
    REPEATED_POOR_PERFORMANCE("Repeated Poor Performance"),
    HIGH_RISK_SUPPLIER("High Risk Supplier"),
    CRITERIA_DEFICIENCY("Criteria Deficiency");

    private final String displayName;

    AlertType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
