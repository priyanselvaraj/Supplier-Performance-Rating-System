package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.RecommendationPriority;

public class AiRecommendationResponse {
    private String id;
    private Long supplierId;
    private String criterionName;
    private Double currentScore;
    private Double maxScore;
    private Double percentage;
    private RecommendationPriority priority;
    private String title;
    private String recommendation;
    private String actionableSteps;

    public AiRecommendationResponse() {}

    public AiRecommendationResponse(String id, Long supplierId, String criterionName, Double currentScore, Double maxScore, Double percentage, RecommendationPriority priority, String title, String recommendation, String actionableSteps) {
        this.id = id;
        this.supplierId = supplierId;
        this.criterionName = criterionName;
        this.currentScore = currentScore;
        this.maxScore = maxScore;
        this.percentage = percentage;
        this.priority = priority;
        this.title = title;
        this.recommendation = recommendation;
        this.actionableSteps = actionableSteps;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Long supplierId;
        private String criterionName;
        private Double currentScore;
        private Double maxScore;
        private Double percentage;
        private RecommendationPriority priority;
        private String title;
        private String recommendation;
        private String actionableSteps;

        public Builder id(String id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder criterionName(String criterionName) { this.criterionName = criterionName; return this; }
        public Builder currentScore(Double currentScore) { this.currentScore = currentScore; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder percentage(Double percentage) { this.percentage = percentage; return this; }
        public Builder priority(RecommendationPriority priority) { this.priority = priority; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder recommendation(String rec) { this.recommendation = rec; return this; }
        public Builder actionableSteps(String steps) { this.actionableSteps = steps; return this; }

        public AiRecommendationResponse build() {
            return new AiRecommendationResponse(id, supplierId, criterionName, currentScore, maxScore, percentage, priority, title, recommendation, actionableSteps);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }
    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public RecommendationPriority getPriority() { return priority; }
    public void setPriority(RecommendationPriority priority) { this.priority = priority; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getActionableSteps() { return actionableSteps; }
    public void setActionableSteps(String actionableSteps) { this.actionableSteps = actionableSteps; }
}
