package com.supplier.sprsystem.dto.ai;

import com.supplier.sprsystem.model.entity.RecommendationDecisionStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RecommendationDecisionRequest {

    private String recommendationRef;

    @NotNull(message = "Decision status is required (ACCEPTED, DISMISSED, ACTION_CREATED)")
    private RecommendationDecisionStatus status;

    private String decisionNotes;

    // Optional fields when converting recommendation into an improvement action
    private String actionTitle;
    private String actionDescription;
    private LocalDate targetCompletionDate;

    public RecommendationDecisionRequest() {}

    public RecommendationDecisionRequest(String recommendationRef, RecommendationDecisionStatus status, String decisionNotes, String actionTitle, String actionDescription, LocalDate targetCompletionDate) {
        this.recommendationRef = recommendationRef;
        this.status = status;
        this.decisionNotes = decisionNotes;
        this.actionTitle = actionTitle;
        this.actionDescription = actionDescription;
        this.targetCompletionDate = targetCompletionDate;
    }

    public String getRecommendationRef() { return recommendationRef; }
    public void setRecommendationRef(String recommendationRef) { this.recommendationRef = recommendationRef; }
    public RecommendationDecisionStatus getStatus() { return status; }
    public void setStatus(RecommendationDecisionStatus status) { this.status = status; }
    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
    public String getActionTitle() { return actionTitle; }
    public void setActionTitle(String actionTitle) { this.actionTitle = actionTitle; }
    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }
    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDate targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }
}
