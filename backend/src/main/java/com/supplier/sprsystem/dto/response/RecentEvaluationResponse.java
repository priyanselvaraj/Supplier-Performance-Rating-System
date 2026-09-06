package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.EvaluationStatus;

import java.time.LocalDate;

public class RecentEvaluationResponse {

    private Long evaluationId;
    private String evaluationCode;
    private Long supplierId;
    private String supplierName;
    private String evaluatorName;
    private LocalDate evaluationDate;
    private EvaluationStatus status;
    private Double totalScore;

    public RecentEvaluationResponse() {}

    public RecentEvaluationResponse(Long evaluationId, String evaluationCode, Long supplierId, String supplierName, String evaluatorName, LocalDate evaluationDate, EvaluationStatus status, Double totalScore) {
        this.evaluationId = evaluationId;
        this.evaluationCode = evaluationCode;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.evaluatorName = evaluatorName;
        this.evaluationDate = evaluationDate;
        this.status = status;
        this.totalScore = totalScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long evaluationId;
        private String evaluationCode;
        private Long supplierId;
        private String supplierName;
        private String evaluatorName;
        private LocalDate evaluationDate;
        private EvaluationStatus status;
        private Double totalScore;

        public Builder evaluationId(Long evaluationId) { this.evaluationId = evaluationId; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder evaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder totalScore(Double totalScore) { this.totalScore = totalScore; return this; }

        public RecentEvaluationResponse build() {
            return new RecentEvaluationResponse(evaluationId, evaluationCode, supplierId, supplierName, evaluatorName, evaluationDate, status, totalScore);
        }
    }

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getEvaluatorName() { return evaluatorName; }
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
}
