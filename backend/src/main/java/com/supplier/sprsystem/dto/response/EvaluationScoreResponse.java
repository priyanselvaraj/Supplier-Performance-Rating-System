package com.supplier.sprsystem.dto.response;

public class EvaluationScoreResponse {

    private Long id;
    private Long criteriaId;
    private String criteriaName;
    private String criteriaCode;
    private Double scoreObtained;
    private Double score;
    private Double maxScore;
    private Double weight;
    private Double weightedScore;
    private String remarks;
    private String comments;

    public EvaluationScoreResponse() {}

    public EvaluationScoreResponse(Long id, Long criteriaId, String criteriaName, String criteriaCode, Double scoreObtained, Double maxScore, Double weight, Double weightedScore, String remarks) {
        this.id = id;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.criteriaCode = criteriaCode;
        this.scoreObtained = scoreObtained;
        this.score = scoreObtained;
        this.maxScore = maxScore;
        this.weight = weight;
        this.weightedScore = weightedScore;
        this.remarks = remarks;
        this.comments = remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long criteriaId;
        private String criteriaName;
        private String criteriaCode;
        private Double scoreObtained;
        private Double maxScore;
        private Double weight;
        private Double weightedScore;
        private String remarks;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder criteriaId(Long criteriaId) { this.criteriaId = criteriaId; return this; }
        public Builder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
        public Builder criteriaCode(String criteriaCode) { this.criteriaCode = criteriaCode; return this; }
        public Builder scoreObtained(Double scoreObtained) { this.scoreObtained = scoreObtained; return this; }
        public Builder score(Double score) { this.scoreObtained = score; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder weight(Double weight) { this.weight = weight; return this; }
        public Builder weightedScore(Double weightedScore) { this.weightedScore = weightedScore; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }
        public Builder comments(String comments) { this.remarks = comments; return this; }

        public EvaluationScoreResponse build() {
            return new EvaluationScoreResponse(id, criteriaId, criteriaName, criteriaCode, scoreObtained, maxScore, weight, weightedScore, remarks);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCriteriaId() { return criteriaId; }
    public void setCriteriaId(Long criteriaId) { this.criteriaId = criteriaId; }
    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
    public String getCriteriaCode() { return criteriaCode; }
    public void setCriteriaCode(String criteriaCode) { this.criteriaCode = criteriaCode; }
    public Double getScoreObtained() { return scoreObtained; }
    public void setScoreObtained(Double scoreObtained) {
        this.scoreObtained = scoreObtained;
        this.score = scoreObtained;
    }
    public Double getScore() { return scoreObtained; }
    public void setScore(Double score) {
        this.scoreObtained = score;
        this.score = score;
    }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.comments = remarks;
    }
    public String getComments() { return remarks; }
    public void setComments(String comments) {
        this.remarks = comments;
        this.comments = comments;
    }
}
