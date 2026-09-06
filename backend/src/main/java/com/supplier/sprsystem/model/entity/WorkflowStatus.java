package com.supplier.sprsystem.model.entity;

public enum WorkflowStatus {
    PENDING("Pending Initial Review"),
    IN_PROGRESS("In Progress"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ESCALATED("Escalated"),
    CANCELLED("Cancelled");

    private final String displayName;

    WorkflowStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
