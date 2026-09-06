package com.supplier.sprsystem.dto.external;

import java.time.LocalDateTime;

public class ExternalReportSummaryDto {

    private LocalDateTime generatedAt;
    private Long totalSuppliers;
    private Long activeSuppliers;
    private Double averagePerformanceScore;
    private Long excellentCount;
    private Long goodCount;
    private Long averageCount;
    private Long poorCount;
    private Long highRiskCount;
    private Long completedEvaluationsCount;

    public ExternalReportSummaryDto() {
    }

    public ExternalReportSummaryDto(LocalDateTime generatedAt, Long totalSuppliers, Long activeSuppliers,
                                    Double averagePerformanceScore, Long excellentCount, Long goodCount,
                                    Long averageCount, Long poorCount, Long highRiskCount,
                                    Long completedEvaluationsCount) {
        this.generatedAt = generatedAt;
        this.totalSuppliers = totalSuppliers;
        this.activeSuppliers = activeSuppliers;
        this.averagePerformanceScore = averagePerformanceScore;
        this.excellentCount = excellentCount;
        this.goodCount = goodCount;
        this.averageCount = averageCount;
        this.poorCount = poorCount;
        this.highRiskCount = highRiskCount;
        this.completedEvaluationsCount = completedEvaluationsCount;
    }

    // Getters and Setters
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(Long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public Long getActiveSuppliers() {
        return activeSuppliers;
    }

    public void setActiveSuppliers(Long activeSuppliers) {
        this.activeSuppliers = activeSuppliers;
    }

    public Double getAveragePerformanceScore() {
        return averagePerformanceScore;
    }

    public void setAveragePerformanceScore(Double averagePerformanceScore) {
        this.averagePerformanceScore = averagePerformanceScore;
    }

    public Long getExcellentCount() {
        return excellentCount;
    }

    public void setExcellentCount(Long excellentCount) {
        this.excellentCount = excellentCount;
    }

    public Long getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Long goodCount) {
        this.goodCount = goodCount;
    }

    public Long getAverageCount() {
        return averageCount;
    }

    public void setAverageCount(Long averageCount) {
        this.averageCount = averageCount;
    }

    public Long getPoorCount() {
        return poorCount;
    }

    public void setPoorCount(Long poorCount) {
        this.poorCount = poorCount;
    }

    public Long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(Long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }

    public Long getCompletedEvaluationsCount() {
        return completedEvaluationsCount;
    }

    public void setCompletedEvaluationsCount(Long completedEvaluationsCount) {
        this.completedEvaluationsCount = completedEvaluationsCount;
    }
}
