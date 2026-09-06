package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.EvaluationStatus;

import java.time.LocalDate;
import java.util.List;

public class SupplierEvaluationReportResponse {

    private Long evaluationId;
    private String evaluationCode;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String categoryName;
    private String evaluatorName;
    private LocalDate evaluationDate;
    private String evaluationPeriod;
    private EvaluationStatus status;
    private Double totalScore;
    private String generalComments;
    private String strengths;
    private String areasForImprovement;
    private String recommendation;
    private List<CriteriaScoreReportResponse> criteriaScores;

    public SupplierEvaluationReportResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long evaluationId;
        private String evaluationCode;
        private Long supplierId;
        private String supplierCode;
        private String supplierName;
        private String categoryName;
        private String evaluatorName;
        private LocalDate evaluationDate;
        private String evaluationPeriod;
        private EvaluationStatus status;
        private Double totalScore;
        private String generalComments;
        private String strengths;
        private String areasForImprovement;
        private String recommendation;
        private List<CriteriaScoreReportResponse> criteriaScores;

        public Builder evaluationId(Long evaluationId) { this.evaluationId = evaluationId; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder evaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder evaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder totalScore(Double totalScore) { this.totalScore = totalScore; return this; }
        public Builder generalComments(String generalComments) { this.generalComments = generalComments; return this; }
        public Builder strengths(String strengths) { this.strengths = strengths; return this; }
        public Builder areasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder criteriaScores(List<CriteriaScoreReportResponse> criteriaScores) { this.criteriaScores = criteriaScores; return this; }

        public SupplierEvaluationReportResponse build() {
            SupplierEvaluationReportResponse r = new SupplierEvaluationReportResponse();
            r.evaluationId = this.evaluationId;
            r.evaluationCode = this.evaluationCode;
            r.supplierId = this.supplierId;
            r.supplierCode = this.supplierCode;
            r.supplierName = this.supplierName;
            r.categoryName = this.categoryName;
            r.evaluatorName = this.evaluatorName;
            r.evaluationDate = this.evaluationDate;
            r.evaluationPeriod = this.evaluationPeriod;
            r.status = this.status;
            r.totalScore = this.totalScore;
            r.generalComments = this.generalComments;
            r.strengths = this.strengths;
            r.areasForImprovement = this.areasForImprovement;
            r.recommendation = this.recommendation;
            r.criteriaScores = this.criteriaScores;
            return r;
        }
    }

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getEvaluatorName() { return evaluatorName; }
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public List<CriteriaScoreReportResponse> getCriteriaScores() { return criteriaScores; }
    public void setCriteriaScores(List<CriteriaScoreReportResponse> criteriaScores) { this.criteriaScores = criteriaScores; }
}
