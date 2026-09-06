package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.RatingCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EvaluationResponse {

    private Long id;
    private String evaluationCode;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String supplierCategoryName;
    private Long evaluatorId;
    private String evaluatorName;
    private String evaluatorUsername;
    private LocalDate evaluationDate;
    private String evaluationPeriod;
    private EvaluationStatus status = EvaluationStatus.COMPLETED;
    private Double totalWeightedScore = 0.0;
    private Double totalScore = 0.0;
    private RatingCategory ratingCategory = RatingCategory.UNRATED;
    private String generalComments;
    private String comments;
    private String strengths;
    private String areasForImprovement;
    private String recommendation;
    private List<EvaluationScoreResponse> scores;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EvaluationResponse() {}

    public EvaluationResponse(Long id, String evaluationCode, Long supplierId, String supplierCode, String supplierName, String supplierCategoryName, Long evaluatorId, String evaluatorName, String evaluatorUsername, LocalDate evaluationDate, String evaluationPeriod, EvaluationStatus status, Double totalWeightedScore, RatingCategory ratingCategory, String generalComments, String strengths, String areasForImprovement, String recommendation, List<EvaluationScoreResponse> scores, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.evaluationCode = evaluationCode;
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.supplierCategoryName = supplierCategoryName;
        this.evaluatorId = evaluatorId;
        this.evaluatorName = evaluatorName;
        this.evaluatorUsername = evaluatorUsername;
        this.evaluationDate = evaluationDate;
        this.evaluationPeriod = evaluationPeriod;
        this.status = status != null ? status : EvaluationStatus.COMPLETED;
        this.totalWeightedScore = totalWeightedScore != null ? totalWeightedScore : 0.0;
        this.totalScore = this.totalWeightedScore;
        this.ratingCategory = ratingCategory != null ? ratingCategory : RatingCategory.UNRATED;
        this.generalComments = generalComments;
        this.comments = generalComments;
        this.strengths = strengths;
        this.areasForImprovement = areasForImprovement;
        this.recommendation = recommendation;
        this.scores = scores;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String evaluationCode;
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String supplierCategoryName;
        private Long evaluatorId;
        private String evaluatorName;
        private String evaluatorUsername;
        private LocalDate evaluationDate;
        private String evaluationPeriod;
        private EvaluationStatus status = EvaluationStatus.COMPLETED;
        private Double totalWeightedScore = 0.0;
        private RatingCategory ratingCategory = RatingCategory.UNRATED;
        private String generalComments;
        private String strengths;
        private String areasForImprovement;
        private String recommendation;
        private List<EvaluationScoreResponse> scores;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder supplierCategoryName(String supplierCategoryName) { this.supplierCategoryName = supplierCategoryName; return this; }
        public Builder evaluatorId(Long evaluatorId) { this.evaluatorId = evaluatorId; return this; }
        public Builder evaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; return this; }
        public Builder evaluatorUsername(String evaluatorUsername) { this.evaluatorUsername = evaluatorUsername; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder evaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder totalWeightedScore(Double totalWeightedScore) { this.totalWeightedScore = totalWeightedScore; return this; }
        public Builder totalScore(Double totalScore) { this.totalWeightedScore = totalScore; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder generalComments(String generalComments) { this.generalComments = generalComments; return this; }
        public Builder comments(String comments) { this.generalComments = comments; return this; }
        public Builder strengths(String strengths) { this.strengths = strengths; return this; }
        public Builder areasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder scores(List<EvaluationScoreResponse> scores) { this.scores = scores; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public EvaluationResponse build() {
            return new EvaluationResponse(id, evaluationCode, supplierId, supplierCode, supplierName, supplierCategoryName, evaluatorId, evaluatorName, evaluatorUsername, evaluationDate, evaluationPeriod, status, totalWeightedScore, ratingCategory, generalComments, strengths, areasForImprovement, recommendation, scores, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCategoryName() { return supplierCategoryName; }
    public void setSupplierCategoryName(String supplierCategoryName) { this.supplierCategoryName = supplierCategoryName; }
    public Long getEvaluatorId() { return evaluatorId; }
    public void setEvaluatorId(Long evaluatorId) { this.evaluatorId = evaluatorId; }
    public String getEvaluatorName() { return evaluatorName; }
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }
    public String getEvaluatorUsername() { return evaluatorUsername; }
    public void setEvaluatorUsername(String evaluatorUsername) { this.evaluatorUsername = evaluatorUsername; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public Double getTotalWeightedScore() { return totalWeightedScore; }
    public void setTotalWeightedScore(Double totalWeightedScore) {
        this.totalWeightedScore = totalWeightedScore;
        this.totalScore = totalWeightedScore;
    }
    public Double getTotalScore() { return totalWeightedScore; }
    public void setTotalScore(Double totalScore) {
        this.totalWeightedScore = totalScore;
        this.totalScore = totalScore;
    }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) {
        this.generalComments = generalComments;
        this.comments = generalComments;
    }
    public String getComments() { return getGeneralComments(); }
    public void setComments(String comments) {
        setGeneralComments(comments);
    }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public List<EvaluationScoreResponse> getScores() { return scores; }
    public void setScores(List<EvaluationScoreResponse> scores) { this.scores = scores; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
