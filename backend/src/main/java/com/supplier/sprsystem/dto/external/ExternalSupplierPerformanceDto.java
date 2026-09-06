package com.supplier.sprsystem.dto.external;

import java.time.LocalDateTime;

public class ExternalSupplierPerformanceDto {

    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private Double overallScore;
    private String ratingCategory;
    private Double riskScore;
    private String riskLevel;
    private Integer evaluationCount;
    private Double averageQualityScore;
    private Double averageDeliveryScore;
    private Double averageCostScore;
    private Double averageServiceScore;
    private LocalDateTime lastEvaluatedAt;
    private LocalDateTime lastUpdated;

    public ExternalSupplierPerformanceDto() {
    }

    public ExternalSupplierPerformanceDto(Long supplierId, String supplierCode, String supplierName,
                                          Double overallScore, String ratingCategory,
                                          Double riskScore, String riskLevel,
                                          Integer evaluationCount, Double averageQualityScore,
                                          Double averageDeliveryScore, Double averageCostScore,
                                          Double averageServiceScore, LocalDateTime lastEvaluatedAt,
                                          LocalDateTime lastUpdated) {
        this.supplierId = supplierId;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.overallScore = overallScore;
        this.ratingCategory = ratingCategory;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.evaluationCount = evaluationCount;
        this.averageQualityScore = averageQualityScore;
        this.averageDeliveryScore = averageDeliveryScore;
        this.averageCostScore = averageCostScore;
        this.averageServiceScore = averageServiceScore;
        this.lastEvaluatedAt = lastEvaluatedAt;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
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

    public String getName() {
        return supplierName;
    }

    public void setName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(Integer evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    public Double getAverageQualityScore() {
        return averageQualityScore;
    }

    public void setAverageQualityScore(Double averageQualityScore) {
        this.averageQualityScore = averageQualityScore;
    }

    public Double getAverageDeliveryScore() {
        return averageDeliveryScore;
    }

    public void setAverageDeliveryScore(Double averageDeliveryScore) {
        this.averageDeliveryScore = averageDeliveryScore;
    }

    public Double getAverageCostScore() {
        return averageCostScore;
    }

    public void setAverageCostScore(Double averageCostScore) {
        this.averageCostScore = averageCostScore;
    }

    public Double getAverageServiceScore() {
        return averageServiceScore;
    }

    public void setAverageServiceScore(Double averageServiceScore) {
        this.averageServiceScore = averageServiceScore;
    }

    public LocalDateTime getLastEvaluatedAt() {
        return lastEvaluatedAt;
    }

    public void setLastEvaluatedAt(LocalDateTime lastEvaluatedAt) {
        this.lastEvaluatedAt = lastEvaluatedAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
