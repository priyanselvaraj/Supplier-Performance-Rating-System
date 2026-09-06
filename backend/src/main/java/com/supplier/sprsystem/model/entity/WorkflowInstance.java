package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_instances",
        indexes = {
                @Index(name = "idx_wf_inst_status", columnList = "status"),
                @Index(name = "idx_wf_inst_type", columnList = "workflow_type"),
                @Index(name = "idx_wf_inst_resource", columnList = "related_resource_type, related_resource_id"),
                @Index(name = "idx_wf_inst_initiator", columnList = "initiated_by_id")
        })
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_type", nullable = false, length = 50)
    private WorkflowType workflowType;

    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 80)
    @Column(name = "related_resource_type", nullable = false, length = 80)
    private String relatedResourceType;

    @Column(name = "related_resource_id", nullable = false)
    private Long relatedResourceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkflowStatus status = WorkflowStatus.PENDING;

    @Column(name = "current_step_order", nullable = false)
    private int currentStepOrder = 1;

    @Column(name = "total_steps", nullable = false)
    private int totalSteps = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by_id", nullable = false)
    private User initiatedBy;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC, id ASC")
    private List<ApprovalTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("escalatedAt DESC")
    private List<WorkflowEscalation> escalations = new ArrayList<>();

    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC")
    private List<WorkflowAuditLog> auditLogs = new ArrayList<>();

    public WorkflowInstance() {}

    public WorkflowInstance(Long id, WorkflowDefinition workflowDefinition, WorkflowType workflowType, String title, String relatedResourceType, Long relatedResourceId, WorkflowStatus status, int currentStepOrder, int totalSteps, User initiatedBy, LocalDateTime startedAt, LocalDateTime completedAt, String rejectionReason, String metadata, List<ApprovalTask> tasks, List<WorkflowEscalation> escalations, List<WorkflowAuditLog> auditLogs) {
        this.id = id;
        this.workflowDefinition = workflowDefinition;
        this.workflowType = workflowType;
        this.title = title;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.status = status != null ? status : WorkflowStatus.PENDING;
        this.currentStepOrder = currentStepOrder;
        this.totalSteps = totalSteps;
        this.initiatedBy = initiatedBy;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.rejectionReason = rejectionReason;
        this.metadata = metadata;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.escalations = escalations != null ? escalations : new ArrayList<>();
        this.auditLogs = auditLogs != null ? auditLogs : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkflowDefinition workflowDefinition;
        private WorkflowType workflowType;
        private String title;
        private String relatedResourceType;
        private Long relatedResourceId;
        private WorkflowStatus status = WorkflowStatus.PENDING;
        private int currentStepOrder = 1;
        private int totalSteps = 1;
        private User initiatedBy;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String rejectionReason;
        private String metadata;
        private List<ApprovalTask> tasks = new ArrayList<>();
        private List<WorkflowEscalation> escalations = new ArrayList<>();
        private List<WorkflowAuditLog> auditLogs = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowDefinition(WorkflowDefinition workflowDefinition) { this.workflowDefinition = workflowDefinition; return this; }
        public Builder workflowType(WorkflowType workflowType) { this.workflowType = workflowType; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder relatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; return this; }
        public Builder relatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; return this; }
        public Builder status(WorkflowStatus status) { this.status = status; return this; }
        public Builder currentStepOrder(int currentStepOrder) { this.currentStepOrder = currentStepOrder; return this; }
        public Builder totalSteps(int totalSteps) { this.totalSteps = totalSteps; return this; }
        public Builder initiatedBy(User initiatedBy) { this.initiatedBy = initiatedBy; return this; }
        public Builder startedAt(LocalDateTime startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder tasks(List<ApprovalTask> tasks) { this.tasks = tasks; return this; }
        public Builder escalations(List<WorkflowEscalation> escalations) { this.escalations = escalations; return this; }
        public Builder auditLogs(List<WorkflowAuditLog> auditLogs) { this.auditLogs = auditLogs; return this; }

        public WorkflowInstance build() {
            return new WorkflowInstance(id, workflowDefinition, workflowType, title, relatedResourceType, relatedResourceId, status, currentStepOrder, totalSteps, initiatedBy, startedAt, completedAt, rejectionReason, metadata, tasks, escalations, auditLogs);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
    }

    public void addTask(ApprovalTask task) {
        tasks.add(task);
        task.setWorkflowInstance(this);
    }

    public void addEscalation(WorkflowEscalation escalation) {
        escalations.add(escalation);
        escalation.setWorkflowInstance(this);
    }

    public void addAuditLog(WorkflowAuditLog log) {
        auditLogs.add(log);
        log.setWorkflowInstance(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkflowDefinition getWorkflowDefinition() { return workflowDefinition; }
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) { this.workflowDefinition = workflowDefinition; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }
    public int getCurrentStepOrder() { return currentStepOrder; }
    public void setCurrentStepOrder(int currentStepOrder) { this.currentStepOrder = currentStepOrder; }
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    public User getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(User initiatedBy) { this.initiatedBy = initiatedBy; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public List<ApprovalTask> getTasks() { return tasks; }
    public void setTasks(List<ApprovalTask> tasks) { this.tasks = tasks; }
    public List<WorkflowEscalation> getEscalations() { return escalations; }
    public void setEscalations(List<WorkflowEscalation> escalations) { this.escalations = escalations; }
    public List<WorkflowAuditLog> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<WorkflowAuditLog> auditLogs) { this.auditLogs = auditLogs; }
}
