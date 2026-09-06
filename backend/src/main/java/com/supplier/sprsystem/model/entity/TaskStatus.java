package com.supplier.sprsystem.model.entity;

public enum TaskStatus {
    PENDING("Pending Action"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ESCALATED("Escalated"),
    EXPIRED("Expired"),
    SKIPPED("Skipped");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
