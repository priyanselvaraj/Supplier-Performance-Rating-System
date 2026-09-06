package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_tasks",
        indexes = {
                @Index(name = "idx_task_instance", columnList = "workflow_instance_id"),
                @Index(name = "idx_task_status", columnList = "status"),
                @Index(name = "idx_task_assigned_user", columnList = "assigned_user_id"),
                @Index(name = "idx_task_required_role", columnList = "required_role"),
                @Index(name = "idx_task_due_at", columnList = "due_at")
        })
public class ApprovalTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", nullable = false)
    private WorkflowInstance workflowInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_step_id")
    private WorkflowStep workflowStep;

    @Column(name = "step_order", nullable = false)
    private int stepOrder = 1;

    @NotBlank
    @Size(max = 120)
    @Column(name = "step_name", nullable = false, length = 120)
    private String stepName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "required_role", nullable = false, length = 50)
    private ERole requiredRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actioned_by_id")
    private User actionedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(nullable = false)
    private boolean escalated = false;

    public ApprovalTask() {}

    public ApprovalTask(Long id, WorkflowInstance workflowInstance, WorkflowStep workflowStep, int stepOrder, String stepName, ERole requiredRole, User assignedUser, User actionedBy, TaskStatus status, LocalDateTime assignedAt, LocalDateTime dueAt, LocalDateTime actionedAt, String comments, boolean escalated) {
        this.id = id;
        this.workflowInstance = workflowInstance;
        this.workflowStep = workflowStep;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.assignedUser = assignedUser;
        this.actionedBy = actionedBy;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.assignedAt = assignedAt;
        this.dueAt = dueAt;
        this.actionedAt = actionedAt;
        this.comments = comments;
        this.escalated = escalated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkflowInstance workflowInstance;
        private WorkflowStep workflowStep;
        private int stepOrder = 1;
        private String stepName;
        private ERole requiredRole;
        private User assignedUser;
        private User actionedBy;
        private TaskStatus status = TaskStatus.PENDING;
        private LocalDateTime assignedAt;
        private LocalDateTime dueAt;
        private LocalDateTime actionedAt;
        private String comments;
        private boolean escalated = false;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; return this; }
        public Builder workflowStep(WorkflowStep workflowStep) { this.workflowStep = workflowStep; return this; }
        public Builder stepOrder(int stepOrder) { this.stepOrder = stepOrder; return this; }
        public Builder stepName(String stepName) { this.stepName = stepName; return this; }
        public Builder requiredRole(ERole requiredRole) { this.requiredRole = requiredRole; return this; }
        public Builder assignedUser(User assignedUser) { this.assignedUser = assignedUser; return this; }
        public Builder actionedBy(User actionedBy) { this.actionedBy = actionedBy; return this; }
        public Builder status(TaskStatus status) { this.status = status; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public Builder dueAt(LocalDateTime dueAt) { this.dueAt = dueAt; return this; }
        public Builder actionedAt(LocalDateTime actionedAt) { this.actionedAt = actionedAt; return this; }
        public Builder comments(String comments) { this.comments = comments; return this; }
        public Builder escalated(boolean escalated) { this.escalated = escalated; return this; }

        public ApprovalTask build() {
            return new ApprovalTask(id, workflowInstance, workflowStep, stepOrder, stepName, requiredRole, assignedUser, actionedBy, status, assignedAt, dueAt, actionedAt, comments, escalated);
        }
    }

    public boolean isOverdue() {
        return status == TaskStatus.PENDING && dueAt != null && LocalDateTime.now().isAfter(dueAt);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkflowInstance getWorkflowInstance() { return workflowInstance; }
    public void setWorkflowInstance(WorkflowInstance workflowInstance) { this.workflowInstance = workflowInstance; }
    public WorkflowStep getWorkflowStep() { return workflowStep; }
    public void setWorkflowStep(WorkflowStep workflowStep) { this.workflowStep = workflowStep; }
    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public ERole getRequiredRole() { return requiredRole; }
    public void setRequiredRole(ERole requiredRole) { this.requiredRole = requiredRole; }
    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) { this.assignedUser = assignedUser; }
    public User getActionedBy() { return actionedBy; }
    public void setActionedBy(User actionedBy) { this.actionedBy = actionedBy; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getDueAt() { return dueAt; }
    public void setDueAt(LocalDateTime dueAt) { this.dueAt = dueAt; }
    public LocalDateTime getActionedAt() { return actionedAt; }
    public void setActionedAt(LocalDateTime actionedAt) { this.actionedAt = actionedAt; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public boolean isEscalated() { return escalated; }
    public void setEscalated(boolean escalated) { this.escalated = escalated; }
}
