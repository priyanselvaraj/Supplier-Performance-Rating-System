package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.DocumentStatus;
import jakarta.validation.constraints.NotNull;

public class DocumentReviewDto {

    @NotNull(message = "Document status is required")
    private DocumentStatus status;

    private String notes;

    public DocumentReviewDto() {}

    public DocumentReviewDto(DocumentStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
