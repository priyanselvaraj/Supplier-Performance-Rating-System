package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierCommunicationCreateRequest {

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank(message = "Message content is required")
    private String message;

    @Size(max = 50)
    private String relatedResourceType;

    private Long relatedResourceId;

    public SupplierCommunicationCreateRequest() {}

    public SupplierCommunicationCreateRequest(String subject, String message, String relatedResourceType, Long relatedResourceId) {
        this.subject = subject;
        this.message = message;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
}
