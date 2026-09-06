package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.WorkflowStatus;
import com.supplier.sprsystem.model.entity.WorkflowType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkflowInstanceResponse {
    private Long id;
    private Long workflowDefinitionId;
    private String workflowDefinitionName;
    private WorkflowType workflowType;
    private String workflowTypeDisplayName;
    private String title;
    private String relatedResourceType;
    private Long relatedResourceId;
    private WorkflowStatus status;
    private String statusDisplayName;
    private int currentStepOrder;
    private int totalSteps;
    private Long initiatedById;
    private String initiatedByName;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String rejectionReason;
    private String metadata;
    private boolean isOverdue;
    private boolean isEscalated;
    private List<ApprovalTaskResponse> tasks = new ArrayList<>();
    private List<WorkflowEscalationResponse> escalations = new ArrayList<>();
    private List<WorkflowAuditLogResponse> auditLogs = new ArrayList<>();

    public WorkflowInstanceResponse() {}

    public WorkflowInstanceResponse(Long id, Long workflowDefinitionId, String workflowDefinitionName, WorkflowType workflowType, String workflowTypeDisplayName, String title, String relatedResourceType, Long relatedResourceId, WorkflowStatus status, String statusDisplayName, int currentStepOrder, int totalSteps, Long initiatedById, String initiatedByName, LocalDateTime startedAt, LocalDateTime completedAt, String rejectionReason, String metadata, boolean isOverdue, boolean isEscalated, List<ApprovalTaskResponse> tasks, List<WorkflowEscalationResponse> escalations, List<WorkflowAuditLogResponse> auditLogs) {
        this.id = id;
        this.workflowDefinitionId = workflowDefinitionId;
        this.workflowDefinitionName = workflowDefinitionName;
        this.workflowType = workflowType;
        this.workflowTypeDisplayName = workflowTypeDisplayName;
        this.title = title;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.currentStepOrder = currentStepOrder;
        this.totalSteps = totalSteps;
        this.initiatedById = initiatedById;
        this.initiatedByName = initiatedByName;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.rejectionReason = rejectionReason;
        this.metadata = metadata;
        this.isOverdue = isOverdue;
        this.isEscalated = isEscalated;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.escalations = escalations != null ? escalations : new ArrayList<>();
        this.auditLogs = auditLogs != null ? auditLogs : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long workflowDefinitionId;
        private String workflowDefinitionName;
        private WorkflowType workflowType;
        private String workflowTypeDisplayName;
        private String title;
        private String relatedResourceType;
        private Long relatedResourceId;
        private WorkflowStatus status;
        private String statusDisplayName;
        private int currentStepOrder;
        private int totalSteps;
        private Long initiatedById;
        private String initiatedByName;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String rejectionReason;
        private String metadata;
        private boolean isOverdue;
        private boolean isEscalated;
        private List<ApprovalTaskResponse> tasks = new ArrayList<>();
        private List<WorkflowEscalationResponse> escalations = new ArrayList<>();
        private List<WorkflowAuditLogResponse> auditLogs = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowDefinitionId(Long workflowDefinitionId) { this.workflowDefinitionId = workflowDefinitionId; return this; }
        public Builder workflowDefinitionName(String workflowDefinitionName) { this.workflowDefinitionName = workflowDefinitionName; return this; }
        public Builder workflowType(WorkflowType workflowType) { this.workflowType = workflowType; return this; }
        public Builder workflowTypeDisplayName(String workflowTypeDisplayName) { this.workflowTypeDisplayName = workflowTypeDisplayName; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder relatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; return this; }
        public Builder relatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; return this; }
        public Builder status(WorkflowStatus status) { this.status = status; return this; }
        public Builder statusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; return this; }
        public Builder currentStepOrder(int currentStepOrder) { this.currentStepOrder = currentStepOrder; return this; }
        public Builder totalSteps(int totalSteps) { this.totalSteps = totalSteps; return this; }
        public Builder initiatedById(Long initiatedById) { this.initiatedById = initiatedById; return this; }
        public Builder initiatedByName(String initiatedByName) { this.initiatedByName = initiatedByName; return this; }
        public Builder startedAt(LocalDateTime startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder isOverdue(boolean isOverdue) { this.isOverdue = isOverdue; return this; }
        public Builder isEscalated(boolean isEscalated) { this.isEscalated = isEscalated; return this; }
        public Builder tasks(List<ApprovalTaskResponse> tasks) { this.tasks = tasks; return this; }
        public Builder escalations(List<WorkflowEscalationResponse> escalations) { this.escalations = escalations; return this; }
        public Builder auditLogs(List<WorkflowAuditLogResponse> auditLogs) { this.auditLogs = auditLogs; return this; }

        public WorkflowInstanceResponse build() {
            return new WorkflowInstanceResponse(id, workflowDefinitionId, workflowDefinitionName, workflowType, workflowTypeDisplayName, title, relatedResourceType, relatedResourceId, status, statusDisplayName, currentStepOrder, totalSteps, initiatedById, initiatedByName, startedAt, completedAt, rejectionReason, metadata, isOverdue, isEscalated, tasks, escalations, auditLogs);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkflowDefinitionId() { return workflowDefinitionId; }
    public void setWorkflowDefinitionId(Long workflowDefinitionId) { this.workflowDefinitionId = workflowDefinitionId; }
    public String getWorkflowDefinitionName() { return workflowDefinitionName; }
    public void setWorkflowDefinitionName(String workflowDefinitionName) { this.workflowDefinitionName = workflowDefinitionName; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public String getWorkflowTypeDisplayName() { return workflowTypeDisplayName; }
    public void setWorkflowTypeDisplayName(String workflowTypeDisplayName) { this.workflowTypeDisplayName = workflowTypeDisplayName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    public int getCurrentStepOrder() { return currentStepOrder; }
    public void setCurrentStepOrder(int currentStepOrder) { this.currentStepOrder = currentStepOrder; }
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    public Long getInitiatedById() { return initiatedById; }
    public void setInitiatedById(Long initiatedById) { this.initiatedById = initiatedById; }
    public String getInitiatedByName() { return initiatedByName; }
    public void setInitiatedByName(String initiatedByName) { this.initiatedByName = initiatedByName; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
    public boolean isEscalated() { return isEscalated; }
    public void setEscalated(boolean escalated) { isEscalated = escalated; }
    public List<ApprovalTaskResponse> getTasks() { return tasks; }
    public void setTasks(List<ApprovalTaskResponse> tasks) { this.tasks = tasks; }
    public List<WorkflowEscalationResponse> getEscalations() { return escalations; }
    public void setEscalations(List<WorkflowEscalationResponse> escalations) { this.escalations = escalations; }
    public List<WorkflowAuditLogResponse> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<WorkflowAuditLogResponse> auditLogs) { this.auditLogs = auditLogs; }
}
