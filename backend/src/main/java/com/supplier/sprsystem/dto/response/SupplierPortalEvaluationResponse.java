package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.EvaluationStatus;
import com.supplier.sprsystem.model.entity.RatingCategory;

import java.time.LocalDate;
import java.util.List;

public class SupplierPortalEvaluationResponse {

    private Long id;
    private String evaluationCode;
    private Long supplierId;
    private String supplierName;
    private LocalDate evaluationDate;
    private String evaluationPeriod;
    private EvaluationStatus status;
    private Double totalWeightedScore;
    private RatingCategory ratingCategory;
    private String strengths;
    private String areasForImprovement;
    private String recommendation;
    private List<SupplierPortalCriteriaScoreResponse> criteriaScores;

    public SupplierPortalEvaluationResponse() {}

    public SupplierPortalEvaluationResponse(Long id, String evaluationCode, Long supplierId, String supplierName, LocalDate evaluationDate, String evaluationPeriod, EvaluationStatus status, Double totalWeightedScore, RatingCategory ratingCategory, String strengths, String areasForImprovement, String recommendation, List<SupplierPortalCriteriaScoreResponse> criteriaScores) {
        this.id = id;
        this.evaluationCode = evaluationCode;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.evaluationDate = evaluationDate;
        this.evaluationPeriod = evaluationPeriod;
        this.status = status;
        this.totalWeightedScore = totalWeightedScore;
        this.ratingCategory = ratingCategory;
        this.strengths = strengths;
        this.areasForImprovement = areasForImprovement;
        this.recommendation = recommendation;
        this.criteriaScores = criteriaScores;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String evaluationCode;
        private Long supplierId;
        private String supplierName;
        private LocalDate evaluationDate;
        private String evaluationPeriod;
        private EvaluationStatus status;
        private Double totalWeightedScore;
        private RatingCategory ratingCategory;
        private String strengths;
        private String areasForImprovement;
        private String recommendation;
        private List<SupplierPortalCriteriaScoreResponse> criteriaScores;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder evaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder totalWeightedScore(Double totalWeightedScore) { this.totalWeightedScore = totalWeightedScore; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder strengths(String strengths) { this.strengths = strengths; return this; }
        public Builder areasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder criteriaScores(List<SupplierPortalCriteriaScoreResponse> criteriaScores) { this.criteriaScores = criteriaScores; return this; }

        public SupplierPortalEvaluationResponse build() {
            return new SupplierPortalEvaluationResponse(id, evaluationCode, supplierId, supplierName, evaluationDate, evaluationPeriod, status, totalWeightedScore, ratingCategory, strengths, areasForImprovement, recommendation, criteriaScores);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public Double getTotalWeightedScore() { return totalWeightedScore; }
    public void setTotalWeightedScore(Double totalWeightedScore) { this.totalWeightedScore = totalWeightedScore; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public List<SupplierPortalCriteriaScoreResponse> getCriteriaScores() { return criteriaScores; }
    public void setCriteriaScores(List<SupplierPortalCriteriaScoreResponse> criteriaScores) { this.criteriaScores = criteriaScores; }
}
