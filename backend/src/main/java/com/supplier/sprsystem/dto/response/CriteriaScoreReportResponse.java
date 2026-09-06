package com.supplier.sprsystem.dto.response;

public class CriteriaScoreReportResponse {

    private Long criteriaId;
    private String criteriaName;
    private String criteriaCode;
    private Double weight;
    private Double rawScore;
    private Double maxScore;
    private Double weightedScore;
    private String comments;

    public CriteriaScoreReportResponse() {}

    public CriteriaScoreReportResponse(Long criteriaId, String criteriaName, String criteriaCode, Double weight, Double rawScore, Double maxScore, Double weightedScore, String comments) {
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.criteriaCode = criteriaCode;
        this.weight = weight;
        this.rawScore = rawScore;
        this.maxScore = maxScore;
        this.weightedScore = weightedScore;
        this.comments = comments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long criteriaId;
        private String criteriaName;
        private String criteriaCode;
        private Double weight;
        private Double rawScore;
        private Double maxScore;
        private Double weightedScore;
        private String comments;

        public Builder criteriaId(Long criteriaId) { this.criteriaId = criteriaId; return this; }
        public Builder criteriaName(String criteriaName) { this.criteriaName = criteriaName; return this; }
        public Builder criteriaCode(String criteriaCode) { this.criteriaCode = criteriaCode; return this; }
        public Builder weight(Double weight) { this.weight = weight; return this; }
        public Builder rawScore(Double rawScore) { this.rawScore = rawScore; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder weightedScore(Double weightedScore) { this.weightedScore = weightedScore; return this; }
        public Builder comments(String comments) { this.comments = comments; return this; }

        public CriteriaScoreReportResponse build() {
            return new CriteriaScoreReportResponse(criteriaId, criteriaName, criteriaCode, weight, rawScore, maxScore, weightedScore, comments);
        }
    }

    public Long getCriteriaId() { return criteriaId; }
    public void setCriteriaId(Long criteriaId) { this.criteriaId = criteriaId; }
    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
    public String getCriteriaCode() { return criteriaCode; }
    public void setCriteriaCode(String criteriaCode) { this.criteriaCode = criteriaCode; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getRawScore() { return rawScore; }
    public void setRawScore(Double rawScore) { this.rawScore = rawScore; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
