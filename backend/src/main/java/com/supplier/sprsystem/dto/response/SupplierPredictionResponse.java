package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.ConfidenceLevel;
import com.supplier.sprsystem.model.entity.PerformanceTrend;

import java.util.List;

public class SupplierPredictionResponse {
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private Double currentScore;
    private Double predictedScore;
    private ConfidenceLevel confidence;
    private PerformanceTrend trend;
    private Double velocityRate;
    private int historicalEvaluationsCount;
    private List<Double> historicalScores;
    private String predictionMethod;
    private String explanation;
    private boolean sufficientData;
    private String statusMessage;

    public SupplierPredictionResponse() {}

    public SupplierPredictionResponse(Long supplierId, String supplierName, String supplierCode, Double currentScore, Double predictedScore, ConfidenceLevel confidence, PerformanceTrend trend, Double velocityRate, int historicalEvaluationsCount, List<Double> historicalScores, String predictionMethod, String explanation, boolean sufficientData, String statusMessage) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierCode = supplierCode;
        this.currentScore = currentScore;
        this.predictedScore = predictedScore;
        this.confidence = confidence;
        this.trend = trend;
        this.velocityRate = velocityRate;
        this.historicalEvaluationsCount = historicalEvaluationsCount;
        this.historicalScores = historicalScores;
        this.predictionMethod = predictionMethod;
        this.explanation = explanation;
        this.sufficientData = sufficientData;
        this.statusMessage = statusMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long supplierId;
        private String supplierName;
        private String supplierCode;
        private Double currentScore;
        private Double predictedScore;
        private ConfidenceLevel confidence;
        private PerformanceTrend trend;
        private Double velocityRate;
        private int historicalEvaluationsCount;
        private List<Double> historicalScores;
        private String predictionMethod;
        private String explanation;
        private boolean sufficientData;
        private String statusMessage;

        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder currentScore(Double currentScore) { this.currentScore = currentScore; return this; }
        public Builder predictedScore(Double predictedScore) { this.predictedScore = predictedScore; return this; }
        public Builder confidence(ConfidenceLevel confidence) { this.confidence = confidence; return this; }
        public Builder trend(PerformanceTrend trend) { this.trend = trend; return this; }
        public Builder velocityRate(Double velocityRate) { this.velocityRate = velocityRate; return this; }
        public Builder historicalEvaluationsCount(int count) { this.historicalEvaluationsCount = count; return this; }
        public Builder historicalScores(List<Double> scores) { this.historicalScores = scores; return this; }
        public Builder predictionMethod(String method) { this.predictionMethod = method; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }
        public Builder sufficientData(boolean sufficient) { this.sufficientData = sufficient; return this; }
        public Builder statusMessage(String msg) { this.statusMessage = msg; return this; }

        public SupplierPredictionResponse build() {
            return new SupplierPredictionResponse(supplierId, supplierName, supplierCode, currentScore, predictedScore, confidence, trend, velocityRate, historicalEvaluationsCount, historicalScores, predictionMethod, explanation, sufficientData, statusMessage);
        }
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public Double getCurrentScore() { return currentScore; }
    public void setCurrentScore(Double currentScore) { this.currentScore = currentScore; }
    public Double getPredictedScore() { return predictedScore; }
    public void setPredictedScore(Double predictedScore) { this.predictedScore = predictedScore; }
    public ConfidenceLevel getConfidence() { return confidence; }
    public void setConfidence(ConfidenceLevel confidence) { this.confidence = confidence; }
    public PerformanceTrend getTrend() { return trend; }
    public void setTrend(PerformanceTrend trend) { this.trend = trend; }
    public Double getVelocityRate() { return velocityRate; }
    public void setVelocityRate(Double velocityRate) { this.velocityRate = velocityRate; }
    public int getHistoricalEvaluationsCount() { return historicalEvaluationsCount; }
    public void setHistoricalEvaluationsCount(int count) { this.historicalEvaluationsCount = count; }
    public List<Double> getHistoricalScores() { return historicalScores; }
    public void setHistoricalScores(List<Double> historicalScores) { this.historicalScores = historicalScores; }
    public String getPredictionMethod() { return predictionMethod; }
    public void setPredictionMethod(String predictionMethod) { this.predictionMethod = predictionMethod; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public boolean isSufficientData() { return sufficientData; }
    public void setSufficientData(boolean sufficientData) { this.sufficientData = sufficientData; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
