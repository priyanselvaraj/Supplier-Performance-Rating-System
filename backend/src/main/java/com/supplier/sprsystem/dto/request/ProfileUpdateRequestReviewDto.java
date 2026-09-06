package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotNull;

public class ProfileUpdateRequestReviewDto {

    @NotNull(message = "Approval status flag is required")
    private Boolean approved;

    private String reviewerNotes;

    public ProfileUpdateRequestReviewDto() {}

    public ProfileUpdateRequestReviewDto(Boolean approved, String reviewerNotes) {
        this.approved = approved;
        this.reviewerNotes = reviewerNotes;
    }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }
}
