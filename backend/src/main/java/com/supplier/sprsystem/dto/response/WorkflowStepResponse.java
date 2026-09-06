package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.ERole;

public class WorkflowStepResponse {
    private Long id;
    private int stepOrder;
    private String stepName;
    private ERole requiredRole;
    private String requiredRoleDisplayName;
    private int slaHours;
    private ERole escalationRole;
    private String escalationRoleDisplayName;
    private boolean autoEscalate;
    private String instructions;

    public WorkflowStepResponse() {}

    public WorkflowStepResponse(Long id, int stepOrder, String stepName, ERole requiredRole, String requiredRoleDisplayName, int slaHours, ERole escalationRole, String escalationRoleDisplayName, boolean autoEscalate, String instructions) {
        this.id = id;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.requiredRoleDisplayName = requiredRoleDisplayName;
        this.slaHours = slaHours;
        this.escalationRole = escalationRole;
        this.escalationRoleDisplayName = escalationRoleDisplayName;
        this.autoEscalate = autoEscalate;
        this.instructions = instructions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private int stepOrder;
        private String stepName;
        private ERole requiredRole;
        private String requiredRoleDisplayName;
        private int slaHours;
        private ERole escalationRole;
        private String escalationRoleDisplayName;
        private boolean autoEscalate;
        private String instructions;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder stepOrder(int stepOrder) { this.stepOrder = stepOrder; return this; }
        public Builder stepName(String stepName) { this.stepName = stepName; return this; }
        public Builder requiredRole(ERole requiredRole) { this.requiredRole = requiredRole; return this; }
        public Builder requiredRoleDisplayName(String requiredRoleDisplayName) { this.requiredRoleDisplayName = requiredRoleDisplayName; return this; }
        public Builder slaHours(int slaHours) { this.slaHours = slaHours; return this; }
        public Builder escalationRole(ERole escalationRole) { this.escalationRole = escalationRole; return this; }
        public Builder escalationRoleDisplayName(String escalationRoleDisplayName) { this.escalationRoleDisplayName = escalationRoleDisplayName; return this; }
        public Builder autoEscalate(boolean autoEscalate) { this.autoEscalate = autoEscalate; return this; }
        public Builder instructions(String instructions) { this.instructions = instructions; return this; }

        public WorkflowStepResponse build() {
            return new WorkflowStepResponse(id, stepOrder, stepName, requiredRole, requiredRoleDisplayName, slaHours, escalationRole, escalationRoleDisplayName, autoEscalate, instructions);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public ERole getRequiredRole() { return requiredRole; }
    public void setRequiredRole(ERole requiredRole) { this.requiredRole = requiredRole; }
    public String getRequiredRoleDisplayName() { return requiredRoleDisplayName; }
    public void setRequiredRoleDisplayName(String requiredRoleDisplayName) { this.requiredRoleDisplayName = requiredRoleDisplayName; }
    public int getSlaHours() { return slaHours; }
    public void setSlaHours(int slaHours) { this.slaHours = slaHours; }
    public ERole getEscalationRole() { return escalationRole; }
    public void setEscalationRole(ERole escalationRole) { this.escalationRole = escalationRole; }
    public String getEscalationRoleDisplayName() { return escalationRoleDisplayName; }
    public void setEscalationRoleDisplayName(String escalationRoleDisplayName) { this.escalationRoleDisplayName = escalationRoleDisplayName; }
    public boolean isAutoEscalate() { return autoEscalate; }
    public void setAutoEscalate(boolean autoEscalate) { this.autoEscalate = autoEscalate; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
