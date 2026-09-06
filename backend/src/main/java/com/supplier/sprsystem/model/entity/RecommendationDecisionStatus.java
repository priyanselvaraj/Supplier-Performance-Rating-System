package com.supplier.sprsystem.model.entity;

public enum RecommendationDecisionStatus {
    PENDING("Pending Human Review"),
    ACCEPTED("Accepted"),
    DISMISSED("Dismissed"),
    ACTION_CREATED("Action Plan Initiated");

    private final String displayName;

    RecommendationDecisionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
