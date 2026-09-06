package com.supplier.sprsystem.dto.response;

import java.time.LocalDateTime;

public class WorkflowAuditLogResponse {
    private Long id;
    private Long workflowInstanceId;
    private String eventType;
    private Long performedById;
    private String performedByName;
    private LocalDateTime timestamp;
    private String description;
    private String details;

    public WorkflowAuditLogResponse() {}

    public WorkflowAuditLogResponse(Long id, Long workflowInstanceId, String eventType, Long performedById, String performedByName, LocalDateTime timestamp, String description, String details) {
        this.id = id;
        this.workflowInstanceId = workflowInstanceId;
        this.eventType = eventType;
        this.performedById = performedById;
        this.performedByName = performedByName;
        this.timestamp = timestamp;
        this.description = description;
        this.details = details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long workflowInstanceId;
        private String eventType;
        private Long performedById;
        private String performedByName;
        private LocalDateTime timestamp;
        private String description;
        private String details;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder performedById(Long performedById) { this.performedById = performedById; return this; }
        public Builder performedByName(String performedByName) { this.performedByName = performedByName; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder details(String details) { this.details = details; return this; }

        public WorkflowAuditLogResponse build() {
            return new WorkflowAuditLogResponse(id, workflowInstanceId, eventType, performedById, performedByName, timestamp, description, details);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Long getPerformedById() { return performedById; }
    public void setPerformedById(Long performedById) { this.performedById = performedById; }
    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
