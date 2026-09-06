package com.supplier.sprsystem.dto.response;

public class SupplierPortalCriteriaScoreResponse {

    private Long criteriaId;
    private String criteriaName;
    private String description;
    private Double weight;
    private Double maxScore;
    private Double scoreObtained;
    private Double weightedScore;
    private String remarks;

    public SupplierPortalCriteriaScoreResponse() {}

    public SupplierPortalCriteriaScoreResponse(Long criteriaId, String criteriaName, String description, Double weight, Double maxScore, Double scoreObtained, Double weightedScore, String remarks) {
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.description = description;
        this.weight = weight;
        this.maxScore = maxScore;
        this.scoreObtained = scoreObtained;
        this.weightedScore = weightedScore;
        this.remarks = remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long criteriaId;
        private String criteriaName;
        private String description;
        private Double weight;
        private Double maxScore;
        private Double scoreObtained;
        private Double weightedScore;
        private String remarks;

        public Builder criteriaId(Long criteriaId) { this.criteriaId = criteriaId; return this; }
        public Builder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder weight(Double weight) { this.weight = weight; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder scoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; return this; }
        public Builder weightedScore(Double weightedScore) { this.weightedScore = weightedScore; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }

        public SupplierPortalCriteriaScoreResponse build() {
            return new SupplierPortalCriteriaScoreResponse(criteriaId, criteriaName, description, weight, maxScore, scoreObtained, weightedScore, remarks);
        }
    }

    public Long getCriteriaId() { return criteriaId; }
    public void setCriteriaId(Long criteriaId) { this.criteriaId = criteriaId; }
    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Double getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; }
    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
