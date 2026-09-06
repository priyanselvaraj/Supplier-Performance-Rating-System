package com.supplier.sprsystem.dto.ai;

import jakarta.validation.constraints.NotNull;

public class AiFeedbackRequest {

    @NotNull(message = "Helpful rating is required")
    private Boolean helpful;

    private String feedbackReason;

    public AiFeedbackRequest() {}

    public AiFeedbackRequest(Boolean helpful, String feedbackReason) {
        this.helpful = helpful;
        this.feedbackReason = feedbackReason;
    }

    public Boolean getHelpful() { return helpful; }
    public void setHelpful(Boolean helpful) { this.helpful = helpful; }
    public String getFeedbackReason() { return feedbackReason; }
    public void setFeedbackReason(String feedbackReason) { this.feedbackReason = feedbackReason; }
}
