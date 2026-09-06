package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_audit_logs",
        indexes = {
                @Index(name = "idx_wf_audit_instance", columnList = "workflow_instance_id"),
                @Index(name = "idx_wf_audit_time", columnList = "timestamp")
        })
public class WorkflowAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", nullable = false)
    private WorkflowInstance workflowInstance;

    @NotBlank
    @Size(max = 60)
    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String details;

    public WorkflowAuditLog() {}

    public WorkflowAuditLog(Long id, WorkflowInstance workflowInstance, String eventType, User performedBy, LocalDateTime timestamp, String description, String details) {
        this.id = id;
        this.workflowInstance = workflowInstance;
        this.eventType = eventType;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.description = description;
        this.details = details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkflowInstance workflowInstance;
        private String eventType;
        private User performedBy;
        private LocalDateTime timestamp;
        private String description;
        private String details;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; return this; }
        public Builder eventType(String eventType) { this.eventType = eventType; return this; }
        public Builder performedBy(User performedBy) { this.performedBy = performedBy; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder details(String details) { this.details = details; return this; }

        public WorkflowAuditLog build() {
            return new WorkflowAuditLog(id, workflowInstance, eventType, performedBy, timestamp, description, details);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkflowInstance getWorkflowInstance() { return workflowInstance; }
    public void setWorkflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
