package com.supplier.sprsystem.dto.ai;

import com.supplier.sprsystem.model.entity.RecommendationPriority;

public class AiWorkflowRecommendationResponse {

    private Long workflowInstanceId;
    private String workflowTitle;
    private String supplierName;
    private String currentStepName;
    private int daysPending;
    private String recommendedAction;
    private String rationale;
    private RecommendationPriority priority;

    public AiWorkflowRecommendationResponse() {}

    public AiWorkflowRecommendationResponse(Long workflowInstanceId, String workflowTitle, String supplierName, String currentStepName, int daysPending, String recommendedAction, String rationale, RecommendationPriority priority) {
        this.workflowInstanceId = workflowInstanceId;
        this.workflowTitle = workflowTitle;
        this.supplierName = supplierName;
        this.currentStepName = currentStepName;
        this.daysPending = daysPending;
        this.recommendedAction = recommendedAction;
        this.rationale = rationale;
        this.priority = priority;
    }

    public Long getWorkflowInstanceId() { return workflowInstanceId; }
    public void setWorkflowInstanceId(Long workflowInstanceId) { this.workflowInstanceId = workflowInstanceId; }
    public String getWorkflowTitle() { return workflowTitle; }
    public void setWorkflowTitle(String workflowTitle) { this.workflowTitle = workflowTitle; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getCurrentStepName() { return currentStepName; }
    public void setCurrentStepName(String currentStepName) { this.currentStepName = currentStepName; }
    public int getDaysPending() { return daysPending; }
    public void setDaysPending(int daysPending) { this.daysPending = daysPending; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public RecommendationPriority getPriority() { return priority; }
    public void setPriority(RecommendationPriority priority) { this.priority = priority; }
}
