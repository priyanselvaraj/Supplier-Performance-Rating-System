package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.Size;

public class ApprovalActionRequest {

    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;

    @Size(max = 2000, message = "Rejection reason cannot exceed 2000 characters")
    private String rejectionReason;

    public ApprovalActionRequest() {}

    public ApprovalActionRequest(String comments, String rejectionReason) {
        this.comments = comments;
        this.rejectionReason = rejectionReason;
    }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
