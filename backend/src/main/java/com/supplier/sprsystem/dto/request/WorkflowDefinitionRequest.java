package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.WorkflowType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class WorkflowDefinitionRequest {

    @NotBlank(message = "Workflow name is required")
    @Size(max = 150)
    private String name;

    @NotNull(message = "Workflow type is required")
    private WorkflowType workflowType;

    private String description;

    private boolean active = true;

    @NotEmpty(message = "At least one workflow step is required")
    @Valid
    private List<WorkflowStepRequest> steps;

    public WorkflowDefinitionRequest() {}

    public WorkflowDefinitionRequest(String name, WorkflowType workflowType, String description, boolean active, List<WorkflowStepRequest> steps) {
        this.name = name;
        this.workflowType = workflowType;
        this.description = description;
        this.active = active;
        this.steps = steps;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<WorkflowStepRequest> getSteps() { return steps; }
    public void setSteps(List<WorkflowStepRequest> steps) { this.steps = steps; }
}
