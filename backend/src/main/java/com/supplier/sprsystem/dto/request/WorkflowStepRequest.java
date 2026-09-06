package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.ERole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WorkflowStepRequest {

    @Min(1)
    private int stepOrder;

    @NotBlank(message = "Step name is required")
    @Size(max = 100)
    private String stepName;

    @NotNull(message = "Required role is required")
    private ERole requiredRole;

    @Min(1)
    private int slaHours = 24;

    private ERole escalationRole = ERole.ROLE_ADMIN;

    private boolean autoEscalate = true;

    private String instructions;

    public WorkflowStepRequest() {}

    public WorkflowStepRequest(int stepOrder, String stepName, ERole requiredRole, int slaHours, ERole escalationRole, boolean autoEscalate, String instructions) {
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.slaHours = slaHours;
        this.escalationRole = escalationRole;
        this.autoEscalate = autoEscalate;
        this.instructions = instructions;
    }

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
