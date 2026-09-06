package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.EvaluationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class EvaluationRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private LocalDate evaluationDate;

    @Size(max = 50, message = "Evaluation period cannot exceed 50 characters")
    private String evaluationPeriod;

    private String generalComments;
    private String comments;

    private String strengths;

    private String areasForImprovement;

    private String recommendation;

    private Boolean draft;

    private EvaluationStatus status;

    @NotEmpty(message = "Evaluation must contain at least one criterion score")
    @Valid
    private List<EvaluationScoreRequest> scores;

    public EvaluationRequest() {}

    public EvaluationRequest(Long supplierId, LocalDate evaluationDate, String evaluationPeriod, String generalComments, String comments, String strengths, String areasForImprovement, String recommendation, Boolean draft, EvaluationStatus status, List<EvaluationScoreRequest> scores) {
        this.supplierId = supplierId;
        this.evaluationDate = evaluationDate;
        this.evaluationPeriod = evaluationPeriod;
        this.generalComments = generalComments != null ? generalComments : comments;
        this.comments = this.generalComments;
        this.strengths = strengths;
        this.areasForImprovement = areasForImprovement;
        this.recommendation = recommendation;
        this.draft = draft;
        this.status = status;
        this.scores = scores;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private LocalDate evaluationDate;
        private String evaluationPeriod;
        private String generalComments;
        private String comments;
        private String strengths;
        private String areasForImprovement;
        private String recommendation;
        private Boolean draft = false;
        private EvaluationStatus status;
        private List<EvaluationScoreRequest> scores;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder evaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; return this; }
        public Builder generalComments(String generalComments) {
            this.generalComments = generalComments;
            this.comments = generalComments;
            return this;
        }
        public Builder comments(String comments) {
            this.comments = comments;
            this.generalComments = comments;
            return this;
        }
        public Builder strengths(String strengths) { this.strengths = strengths; return this; }
        public Builder areasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder draft(Boolean draft) { this.draft = draft; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder scores(List<EvaluationScoreRequest> scores) { this.scores = scores; return this; }

        public EvaluationRequest build() {
            return new EvaluationRequest(supplierId, evaluationDate, evaluationPeriod, generalComments, comments, strengths, areasForImprovement, recommendation, draft, status, scores);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }
    public String getGeneralComments() {
        return generalComments != null ? generalComments : comments;
    }
    public void setGeneralComments(String generalComments) {
        this.generalComments = generalComments;
        this.comments = generalComments;
    }
    public String getComments() {
        return getGeneralComments();
    }
    public void setComments(String comments) {
        setGeneralComments(comments);
    }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public Boolean getDraft() { return draft; }
    public void setDraft(Boolean draft) { this.draft = draft; }
    public boolean isDraft() { return Boolean.TRUE.equals(draft) || status == EvaluationStatus.DRAFT; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public List<EvaluationScoreRequest> getScores() { return scores; }
    public void setScores(List<EvaluationScoreRequest> scores) { this.scores = scores; }
}
