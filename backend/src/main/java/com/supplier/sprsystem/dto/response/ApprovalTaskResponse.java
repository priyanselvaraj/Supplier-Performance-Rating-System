package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.TaskStatus;
import com.supplier.sprsystem.model.entity.WorkflowType;

import java.time.LocalDateTime;

public class ApprovalTaskResponse {
    private Long id;
    private Long workflowInstanceId;
    private WorkflowType workflowType;
    private String workflowTitle;
    private String relatedResourceType;
    private Long relatedResourceId;
    private Long workflowStepId;
    private int stepOrder;
    private String stepName;
    private ERole requiredRole;
    private String requiredRoleDisplayName;
    private Long assignedUserId;
    private String assignedUserName;
    private Long actionedById;
    private String actionedByName;
    private TaskStatus status;
    private String statusDisplayName;
    private LocalDateTime assignedAt;
    private LocalDateTime dueAt;
    private LocalDateTime actionedAt;
    private String comments;
    private boolean isOverdue;
    private boolean escalated;
    private Long hoursRemaining;

    public ApprovalTaskResponse() {}

    public ApprovalTaskResponse(Long id, Long workflowInstanceId, WorkflowType workflowType, String workflowTitle, String relatedResourceType, Long relatedResourceId, Long workflowStepId, int stepOrder, String stepName, ERole requiredRole, String requiredRoleDisplayName, Long assignedUserId, String assignedUserName, Long actionedById, String actionedByName, TaskStatus status, String statusDisplayName, LocalDateTime assignedAt, LocalDateTime dueAt, LocalDateTime actionedAt, String comments, boolean isOverdue, boolean escalated, Long hoursRemaining) {
        this.id = id;
        this.workflowInstanceId = workflowInstanceId;
        this.workflowType = workflowType;
        this.workflowTitle = workflowTitle;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.workflowStepId = workflowStepId;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.requiredRoleDisplayName = requiredRoleDisplayName;
        this.assignedUserId = assignedUserId;
        this.assignedUserName = assignedUserName;
        this.actionedById = actionedById;
        this.actionedByName = actionedByName;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.assignedAt = assignedAt;
        this.dueAt = dueAt;
        this.actionedAt = actionedAt;
        this.comments = comments;
        this.isOverdue = isOverdue;
        this.escalated = escalated;
        this.hoursRemaining = hoursRemaining;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long workflowInstanceId;
        private WorkflowType workflowType;
        private String workflowTitle;
        private String relatedResourceType;
        private Long relatedResourceId;
        private Long workflowStepId;
        private int stepOrder;
        private String stepName;
        private ERole requiredRole;
        private String requiredRoleDisplayName;
        private Long assignedUserId;
        private String assignedUserName;
        private Long actionedById;
        private String actionedByName;
        private TaskStatus status;
        private String statusDisplayName;
        private LocalDateTime assignedAt;
        private LocalDateTime dueAt;
        private LocalDateTime actionedAt;
        private String comments;
        private boolean isOverdue;
        private boolean escalated;
        private Long hoursRemaining;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public Builder workflowType(WorkflowType workflowType) { this.workflowType = workflowType; return this; }
        public Builder workflowTitle(String workflowTitle) { this.workflowTitle = workflowTitle; return this; }
        public Builder relatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; return this; }
        public Builder relatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; return this; }
        public Builder workflowStepId(Long workflowStepId) { this.workflowStepId = workflowStepId; return this; }
        public Builder stepOrder(int stepOrder) { this.stepOrder = stepOrder; return this; }
        public Builder stepName(String stepName) { this.stepName = stepName; return this; }
        public Builder requiredRole(ERole requiredRole) { this.requiredRole = requiredRole; return this; }
        public Builder requiredRoleDisplayName(String requiredRoleDisplayName) { this.requiredRoleDisplayName = requiredRoleDisplayName; return this; }
        public Builder assignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; return this; }
        public Builder assignedUserName(String assignedUserName) { this.assignedUserName = assignedUserName; return this; }
        public Builder actionedById(Long actionedById) { this.actionedById = actionedById; return this; }
        public Builder actionedByName(String actionedByName) { this.actionedByName = actionedByName; return this; }
        public Builder status(TaskStatus status) { this.status = status; return this; }
        public Builder statusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public Builder dueAt(LocalDateTime dueAt) { this.dueAt = dueAt; return this; }
        public Builder actionedAt(LocalDateTime actionedAt) { this.actionedAt = actionedAt; return this; }
        public Builder comments(String comments) { this.comments = comments; return this; }
        public Builder isOverdue(boolean isOverdue) { this.isOverdue = isOverdue; return this; }
        public Builder escalated(boolean escalated) { this.escalated = escalated; return this; }
        public Builder hoursRemaining(Long hoursRemaining) { this.hoursRemaining = hoursRemaining; return this; }

        public ApprovalTaskResponse build() {
            return new ApprovalTaskResponse(id, workflowInstanceId, workflowType, workflowTitle, relatedResourceType, relatedResourceId, workflowStepId, stepOrder, stepName, requiredRole, requiredRoleDisplayName, assignedUserId, assignedUserName, actionedById, actionedByName, status, statusDisplayName, assignedAt, dueAt, actionedAt, comments, isOverdue, escalated, hoursRemaining);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public String getWorkflowTitle() { return workflowTitle; }
    public void setWorkflowTitle(String workflowTitle) { this.workflowTitle = workflowTitle; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
    public Long getWorkflowStepId() { return workflowStepId; }
    public void setWorkflowStepId(Long workflowStepId) { this.workflowStepId = workflowStepId; }
    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public ERole getRequiredRole() { return requiredRole; }
    public void setRequiredRole(ERole requiredRole) { this.requiredRole = requiredRole; }
    public String getRequiredRoleDisplayName() { return requiredRoleDisplayName; }
    public void setRequiredRoleDisplayName(String requiredRoleDisplayName) { this.requiredRoleDisplayName = requiredRoleDisplayName; }
    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }
    public String getAssignedUserName() { return assignedUserName; }
    public void setAssignedUserName(String assignedUserName) { this.assignedUserName = assignedUserName; }
    public Long getActionedById() { return actionedById; }
    public void setActionedById(Long actionedById) { this.actionedById = actionedById; }
    public String getActionedByName() { return actionedByName; }
    public void setActionedByName(String actionedByName) { this.actionedByName = actionedByName; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
    public LocalDateTime getActionedAt() { return actionedAt; }
    public void setActionedAt(LocalDateTime actionedAt) { this.actionedAt = actionedAt; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
    public boolean isEscalated() { return escalated; }
    public void setEscalated(boolean escalated) { this.escalated = escalated; }
    public Long getHoursRemaining() { return hoursRemaining; }
    public void setHoursRemaining(Long hoursRemaining) { this.hoursRemaining = hoursRemaining; }
}
