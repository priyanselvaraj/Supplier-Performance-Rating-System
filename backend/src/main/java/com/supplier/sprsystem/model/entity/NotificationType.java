package com.supplier.sprsystem.model.entity;

public enum NotificationType {
    SYSTEM("System Notification"),
    SUPPLIER("Supplier Update"),
    EVALUATION("Evaluation Activity"),
    RATING("Rating Change"),
    ALERT("Early Warning Alert"),
    AI_INSIGHT("AI Prediction & Risk"),
    IMPROVEMENT_ACTION("Improvement Action"),
    WORKFLOW("Workflow & Approvals"),
    ESCALATION("Workflow Escalation"),
    SECURITY("Security & Authentication");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
