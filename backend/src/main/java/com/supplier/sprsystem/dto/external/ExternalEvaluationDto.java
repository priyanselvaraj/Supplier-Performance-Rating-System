package com.supplier.sprsystem.dto.external;

import java.time.LocalDateTime;

public class ExternalEvaluationDto {

    private Long evaluationId;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String evaluationPeriod;
    private LocalDateTime evaluationDate;
    private Double overallScore;
    private String ratingCategory;
    private String evaluatorName;
    private String status;
    private String comments;

    public ExternalEvaluationDto() {
    }

    public ExternalEvaluationDto(Long evaluationId, Long supplierId, String supplierCode,
                                 String supplierName, String evaluationPeriod,
                                 LocalDateTime evaluationDate, Double overallScore,
                                 String ratingCategory, String evaluatorName,
                                 String status, String comments) {
        this.evaluationId = evaluationId;
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.evaluationPeriod = evaluationPeriod;
        this.evaluationDate = evaluationDate;
        this.overallScore = overallScore;
        this.ratingCategory = ratingCategory;
        this.evaluatorName = evaluatorName;
        this.status = status;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEvaluationPeriod() {
        return evaluationPeriod;
    }

    public void setEvaluationPeriod(String evaluationPeriod) {
        this.evaluationPeriod = evaluationPeriod;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public String getRatingCategory() {
        return ratingCategory;
    }

    public void setRatingCategory(String ratingCategory) {
        this.ratingCategory = ratingCategory;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
