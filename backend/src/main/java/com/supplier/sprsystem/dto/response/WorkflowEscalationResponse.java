package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.ERole;

import java.time.LocalDateTime;

public class WorkflowEscalationResponse {
    private Long id;
    private Long workflowInstanceId;
    private Long approvalTaskId;
    private int escalationLevel;
    private String reason;
    private ERole escalatedFromRole;
    private String escalatedFromRoleDisplayName;
    private ERole escalatedToRole;
    private String escalatedToRoleDisplayName;
    private Long escalatedToUserId;
    private String escalatedToUserName;
    private LocalDateTime escalatedAt;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private String resolutionNotes;

    public WorkflowEscalationResponse() {}

    public WorkflowEscalationResponse(Long id, Long workflowInstanceId, Long approvalTaskId, int escalationLevel, String reason, ERole escalatedFromRole, String escalatedFromRoleDisplayName, ERole escalatedToRole, String escalatedToRoleDisplayName, Long escalatedToUserId, String escalatedToUserName, LocalDateTime escalatedAt, boolean resolved, LocalDateTime resolvedAt, String resolutionNotes) {
        this.id = id;
        this.workflowInstanceId = workflowInstanceId;
        this.approvalTaskId = approvalTaskId;
        this.escalationLevel = escalationLevel;
        this.reason = reason;
        this.escalatedFromRole = escalatedFromRole;
        this.escalatedFromRoleDisplayName = escalatedFromRoleDisplayName;
        this.escalatedToRole = escalatedToRole;
        this.escalatedToRoleDisplayName = escalatedToRoleDisplayName;
        this.escalatedToUserId = escalatedToUserId;
        this.escalatedToUserName = escalatedToUserName;
        this.escalatedAt = escalatedAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
        this.resolutionNotes = resolutionNotes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long workflowInstanceId;
        private Long approvalTaskId;
        private int escalationLevel;
        private String reason;
        private ERole escalatedFromRole;
        private String escalatedFromRoleDisplayName;
        private ERole escalatedToRole;
        private String escalatedToRoleDisplayName;
        private Long escalatedToUserId;
        private String escalatedToUserName;
        private LocalDateTime escalatedAt;
        private boolean resolved;
        private LocalDateTime resolvedAt;
        private String resolutionNotes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; return this; }
        public Builder approvalTaskId(Long approvalTaskId) { this.approvalTaskId = approvalTaskId; return this; }
        public Builder escalationLevel(int escalationLevel) { this.escalationLevel = escalationLevel; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder escalatedFromRole(ERole escalatedFromRole) { this.escalatedFromRole = escalatedFromRole; return this; }
        public Builder escalatedFromRoleDisplayName(String escalatedFromRoleDisplayName) { this.escalatedFromRoleDisplayName = escalatedFromRoleDisplayName; return this; }
        public Builder escalatedToRole(ERole escalatedToRole) { this.escalatedToRole = escalatedToRole; return this; }
        public Builder escalatedToRoleDisplayName(String escalatedToRoleDisplayName) { this.escalatedToRoleDisplayName = escalatedToRoleDisplayName; return this; }
        public Builder escalatedToUserId(Long escalatedToUserId) { this.escalatedToUserId = escalatedToUserId; return this; }
        public Builder escalatedToUserName(String escalatedToUserName) { this.escalatedToUserName = escalatedToUserName; return this; }
        public Builder escalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; return this; }
        public Builder resolved(boolean resolved) { this.resolved = resolved; return this; }
        public Builder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }

        public WorkflowEscalationResponse build() {
            return new WorkflowEscalationResponse(id, workflowInstanceId, approvalTaskId, escalationLevel, reason, escalatedFromRole, escalatedFromRoleDisplayName, escalatedToRole, escalatedToRoleDisplayName, escalatedToUserId, escalatedToUserName, escalatedAt, resolved, resolvedAt, resolutionNotes);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }
    public Long getApprovalTaskId() { return approvalTaskId; }
    public void setApprovalTaskId(Long approvalTaskId) { this.approvalTaskId = approvalTaskId; }
    public int getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(int escalationLevel) { this.escalationLevel = escalationLevel; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public ERole getEscalatedFromRole() { return escalatedFromRole; }
    public void setEscalatedFromRole(ERole escalatedFromRole) { this.escalatedFromRole = escalatedFromRole; }
    public String getEscalatedFromRoleDisplayName() { return escalatedFromRoleDisplayName; }
    public void setEscalatedFromRoleDisplayName(String escalatedFromRoleDisplayName) { this.escalatedFromRoleDisplayName = escalatedFromRoleDisplayName; }
    public ERole getEscalatedToRole() { return escalatedToRole; }
    public void setEscalatedToRole(ERole escalatedToRole) { this.escalatedToRole = escalatedToRole; }
    public String getEscalatedToRoleDisplayName() { return escalatedToRoleDisplayName; }
    public void setEscalatedToRoleDisplayName(String escalatedToRoleDisplayName) { this.escalatedToRoleDisplayName = escalatedToRoleDisplayName; }
    public Long getEscalatedToUserId() { return escalatedToUserId; }
    public void setEscalatedToUserId(Long escalatedToUserId) { this.escalatedToUserId = escalatedToUserId; }
    public String getEscalatedToUserName() { return escalatedToUserName; }
    public void setEscalatedToUserName(String escalatedToUserName) { this.escalatedToUserName = escalatedToUserName; }
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
