package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.WorkflowType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkflowDefinitionResponse {
    private Long id;
    private String name;
    private WorkflowType workflowType;
    private String workflowTypeDisplayName;
    private String description;
    private boolean active;
    private int totalSteps;
    private List<WorkflowStepResponse> steps = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkflowDefinitionResponse() {}

    public WorkflowDefinitionResponse(Long id, String name, WorkflowType workflowType, String workflowTypeDisplayName, String description, boolean active, int totalSteps, List<WorkflowStepResponse> steps, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.workflowType = workflowType;
        this.workflowTypeDisplayName = workflowTypeDisplayName;
        this.description = description;
        this.active = active;
        this.totalSteps = totalSteps;
        this.steps = steps != null ? steps : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private WorkflowType workflowType;
        private String workflowTypeDisplayName;
        private String description;
        private boolean active;
        private int totalSteps;
        private List<WorkflowStepResponse> steps = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder workflowType(WorkflowType workflowType) { this.workflowType = workflowType; return this; }
        public Builder workflowTypeDisplayName(String workflowTypeDisplayName) { this.workflowTypeDisplayName = workflowTypeDisplayName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder totalSteps(int totalSteps) { this.totalSteps = totalSteps; return this; }
        public Builder steps(List<WorkflowStepResponse> steps) { this.steps = steps; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public WorkflowDefinitionResponse build() {
            return new WorkflowDefinitionResponse(id, name, workflowType, workflowTypeDisplayName, description, active, totalSteps, steps, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public String getWorkflowTypeDisplayName() { return workflowTypeDisplayName; }
    public void setWorkflowTypeDisplayName(String workflowTypeDisplayName) { this.workflowTypeDisplayName = workflowTypeDisplayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    public List<WorkflowStepResponse> getSteps() { return steps; }
    public void setSteps(List<WorkflowStepResponse> steps) { this.steps = steps; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
