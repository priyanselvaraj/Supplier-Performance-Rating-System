package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "workflow_steps",
        indexes = {
                @Index(name = "idx_wf_step_def", columnList = "workflow_definition_id"),
                @Index(name = "idx_wf_step_order", columnList = "step_order")
        })
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @Min(1)
    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @NotBlank(message = "Step name is required")
    @Size(max = 100)
    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @NotNull(message = "Required role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "required_role", nullable = false, length = 50)
    private ERole requiredRole;

    @Min(1)
    @Column(name = "sla_hours", nullable = false)
    private int slaHours = 24;

    @Enumerated(EnumType.STRING)
    @Column(name = "escalation_role", length = 50)
    private ERole escalationRole = ERole.ROLE_ADMIN;

    @Column(name = "auto_escalate", nullable = false)
    private boolean autoEscalate = true;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    public WorkflowStep() {}

    public WorkflowStep(Long id, WorkflowDefinition workflowDefinition, int stepOrder, String stepName, ERole requiredRole, int slaHours, ERole escalationRole, boolean autoEscalate, String instructions) {
        this.id = id;
        this.workflowDefinition = workflowDefinition;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.slaHours = slaHours;
        this.escalationRole = escalationRole;
        this.autoEscalate = autoEscalate;
        this.instructions = instructions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkflowDefinition workflowDefinition;
        private int stepOrder;
        private String stepName;
        private ERole requiredRole;
        private int slaHours = 24;
        private ERole escalationRole = ERole.ROLE_ADMIN;
        private boolean autoEscalate = true;
        private String instructions;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder workflowDefinition(WorkflowDefinition workflowDefinition) { this.workflowDefinition = workflowDefinition; return this; }
        public Builder stepOrder(int stepOrder) { this.stepOrder = stepOrder; return this; }
        public Builder stepName(String stepName) { this.stepName = stepName; return this; }
        public Builder requiredRole(ERole requiredRole) { this.requiredRole = requiredRole; return this; }
        public Builder slaHours(int slaHours) { this.slaHours = slaHours; return this; }
        public Builder escalationRole(ERole escalationRole) { this.escalationRole = escalationRole; return this; }
        public Builder autoEscalate(boolean autoEscalate) { this.autoEscalate = autoEscalate; return this; }
        public Builder instructions(String instructions) { this.instructions = instructions; return this; }

        public WorkflowStep build() {
            return new WorkflowStep(id, workflowDefinition, stepOrder, stepName, requiredRole, slaHours, escalationRole, autoEscalate, instructions);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WorkflowDefinition getWorkflowDefinition() { return workflowDefinition; }
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) { this.workflowDefinition = workflowDefinition; }
    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public ERole getRequiredRole() { return requiredRole; }
    public void setRequiredRole(ERole requiredRole) { this.requiredRole = requiredRole; }
    public int getSlaHours() { return slaHours; }
    public void setSlaHours(int slaHours) { this.slaHours = slaHours; }
    public ERole getEscalationRole() { return escalationRole; }
    public void setEscalationRole(ERole escalationRole) { this.escalationRole = escalationRole; }
    public boolean isAutoEscalate() { return autoEscalate; }
    public void setAutoEscalate(boolean autoEscalate) { this.autoEscalate = autoEscalate; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
