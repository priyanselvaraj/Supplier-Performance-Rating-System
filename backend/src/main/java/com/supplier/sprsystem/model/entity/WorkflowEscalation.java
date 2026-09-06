package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_escalations",
        indexes = {
                @Index(name = "idx_esc_instance", columnList = "workflow_instance_id"),
                @Index(name = "idx_esc_task", columnList = "approval_task_id"),
                @Index(name = "idx_esc_time", columnList = "escalated_at")
        })
public class WorkflowEscalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", nullable = false)
    private WorkflowInstance workflowInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_task_id", nullable = false)
    private ApprovalTask approvalTask;

    @Column(name = "escalation_level", nullable = false)
    private int escalationLevel = 1;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "escalated_from_role", nullable = false, length = 50)
    private ERole escalatedFromRole;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "escalated_to_role", nullable = false, length = 50)
    private ERole escalatedToRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_to_user_id")
    private User escalatedToUser;

    @Column(name = "escalated_at", nullable = false)
    private LocalDateTime escalatedAt;

    @Column(nullable = false)
    private boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    public WorkflowEscalation() {}

    public WorkflowEscalation(Long id, WorkflowInstance workflowInstance, ApprovalTask approvalTask, int escalationLevel, String reason, ERole escalatedFromRole, ERole escalatedToRole, User escalatedToUser, LocalDateTime escalatedAt, boolean resolved, LocalDateTime resolvedAt, String resolutionNotes) {
        this.id = id;
        this.workflowInstance = workflowInstance;
        this.approvalTask = approvalTask;
        this.escalationLevel = escalationLevel;
        this.reason = reason;
        this.escalatedFromRole = escalatedFromRole;
        this.escalatedToRole = escalatedToRole;
        this.escalatedToUser = escalatedToUser;
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
        private WorkflowInstance workflowInstance;
        private ApprovalTask approvalTask;
        private int escalationLevel = 1;
        private String reason;
        private ERole escalatedFromRole;
        private ERole escalatedToRole;
        private User escalatedToUser;
        private LocalDateTime escalatedAt;
        private boolean resolved = false;
        private LocalDateTime resolvedAt;
        private String resolutionNotes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; return this; }
        public Builder approvalTask(ApprovalTask approvalTask) { this.approvalTask = approvalTask; return this; }
        public Builder escalationLevel(int escalationLevel) { this.escalationLevel = escalationLevel; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder escalatedFromRole(ERole escalatedFromRole) { this.escalatedFromRole = escalatedFromRole; return this; }
        public Builder escalatedToRole(ERole escalatedToRole) { this.escalatedToRole = escalatedToRole; return this; }
        public Builder escalatedToUser(User escalatedToUser) { this.escalatedToUser = escalatedToUser; return this; }
        public Builder escalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; return this; }
        public Builder resolved(boolean resolved) { this.resolved = resolved; return this; }
        public Builder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }

        public WorkflowEscalation build() {
            return new WorkflowEscalation(id, workflowInstance, approvalTask, escalationLevel, reason, escalatedFromRole, escalatedToRole, escalatedToUser, escalatedAt, resolved, resolvedAt, resolutionNotes);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.escalatedAt == null) {
            this.escalatedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkflowInstance getWorkflowInstance() { return workflowInstance; }
    public void setWorkflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; }
    public ApprovalTask getApprovalTask() { return approvalTask; }
    public void setApprovalTask(ApprovalTask approvalTask) { this.approvalTask = approvalTask; }
    public int getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(int escalationLevel) { this.escalationLevel = escalationLevel; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public ERole getEscalatedFromRole() { return escalatedFromRole; }
    public void setEscalatedFromRole(ERole escalatedFromRole) { this.escalatedFromRole = escalatedFromRole; }
    public ERole getEscalatedToRole() { return escalatedToRole; }
    public void setEscalatedToRole(ERole escalatedToRole) { this.escalatedToRole = escalatedToRole; }
    public User getEscalatedToUser() { return escalatedToUser; }
    public void setEscalatedToUser(User escalatedToUser) { this.escalatedToUser = escalatedToUser; }
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
