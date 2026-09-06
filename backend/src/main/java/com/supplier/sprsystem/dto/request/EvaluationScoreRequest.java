package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class EvaluationScoreRequest {

    @NotNull(message = "Criteria ID is required")
    private Long criteriaId;

    private Double score;
    private Double scoreObtained;

    private String remarks;
    private String comments;

    public EvaluationScoreRequest() {}

    public EvaluationScoreRequest(Long criteriaId, Double score, Double scoreObtained, String remarks, String comments) {
        this.criteriaId = criteriaId;
        this.scoreObtained = scoreObtained != null ? scoreObtained : score;
        this.score = this.scoreObtained;
        this.remarks = remarks != null ? remarks : comments;
        this.comments = this.remarks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long criteriaId;
        private Double score;
        private Double scoreObtained;
        private String remarks;
        private String comments;

        public Builder criteriaId(Long criteriaId) { this.criteriaId = criteriaId; return this; }
        public Builder score(Double score) {
            this.score = score;
            this.scoreObtained = score;
            return this;
        }
        public Builder scoreObtained(Double scoreObtained) {
            this.scoreObtained = scoreObtained;
            this.score = scoreObtained;
            return this;
        }
        public Builder remarks(String remarks) {
            this.remarks = remarks;
            this.comments = remarks;
            return this;
        }
        public Builder comments(String comments) {
            this.comments = comments;
            this.remarks = comments;
            return this;
        }

        public EvaluationScoreRequest build() {
            return new EvaluationScoreRequest(criteriaId, score, scoreObtained, remarks, comments);
        }
    }

    public Long getCriteriaId() { return criteriaId; }
    public void setCriteriaId(Long criteriaId) { this.criteriaId = criteriaId; }

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score cannot be negative")
    @DecimalMax(value = "100.0", message = "Score cannot exceed 100")
    public Double getScoreObtained() {
        return scoreObtained != null ? scoreObtained : score;
    }

    public void setScoreObtained(Double scoreObtained) {
        this.scoreObtained = scoreObtained;
        this.score = scoreObtained;
    }

    public Double getScore() {
        return getScoreObtained();
    }

    public void setScore(Double score) {
        setScoreObtained(score);
    }

    public String getRemarks() {
        return remarks != null ? remarks : comments;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.comments = remarks;
    }

    public String getComments() {
        return getRemarks();
    }

    public void setComments(String comments) {
        setRemarks(comments);
    }
}
