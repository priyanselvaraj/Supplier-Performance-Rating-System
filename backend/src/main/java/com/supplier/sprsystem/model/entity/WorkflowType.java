package com.supplier.sprsystem.model.entity;

public enum WorkflowType {
    SUPPLIER_PROFILE_UPDATE("Supplier Profile Update"),
    SUPPLIER_DOCUMENT_REVIEW("Supplier Document Review"),
    EVALUATION_APPROVAL("Evaluation Approval"),
    IMPROVEMENT_ACTION_CLOSURE("Improvement Action Closure"),
    SUPPLIER_STATUS_CHANGE("Supplier Status Change");

    private final String displayName;

    WorkflowType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
