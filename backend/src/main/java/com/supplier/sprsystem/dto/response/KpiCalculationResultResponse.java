package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.KpiCategory;
import com.supplier.sprsystem.model.entity.KpiStatus;

import java.util.ArrayList;
import java.util.List;

public class KpiCalculationResultResponse {

    private Long kpiId;
    private String kpiCode;
    private String name;
    private String description;
    private KpiCategory category;
    private Double value;
    private String formattedValue;
    private String unit;
    private Double targetValue;
    private Double warningThreshold;
    private Double criticalThreshold;
    private Boolean higherIsBetter;
    private KpiStatus status;
    private String statusReason;
    private Long sampleCount;
    private List<Double> historicalTrend = new ArrayList<>();

    public KpiCalculationResultResponse() {}

    public KpiCalculationResultResponse(Long kpiId, String kpiCode, String name, String description, KpiCategory category, Double value, String formattedValue, String unit, Double targetValue, Double warningThreshold, Double criticalThreshold, Boolean higherIsBetter, KpiStatus status, String statusReason, Long sampleCount, List<Double> historicalTrend) {
        this.kpiId = kpiId;
        this.kpiCode = kpiCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.value = value;
        this.formattedValue = formattedValue;
        this.unit = unit;
        this.targetValue = targetValue;
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;
        this.higherIsBetter = higherIsBetter;
        this.status = status;
        this.statusReason = statusReason;
        this.sampleCount = sampleCount;
        this.historicalTrend = historicalTrend != null ? historicalTrend : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long kpiId;
        private String kpiCode;
        private String name;
        private String description;
        private KpiCategory category;
        private Double value;
        private String formattedValue;
        private String unit;
        private Double targetValue;
        private Double warningThreshold;
        private Double criticalThreshold;
        private Boolean higherIsBetter;
        private KpiStatus status;
        private String statusReason;
        private Long sampleCount;
        private List<Double> historicalTrend = new ArrayList<>();

        public Builder kpiId(Long kpiId) { this.kpiId = kpiId; return this; }
        public Builder kpiCode(String kpiCode) { this.kpiCode = kpiCode; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(KpiCategory category) { this.category = category; return this; }
        public Builder value(Double value) { this.value = value; return this; }
        public Builder formattedValue(String formattedValue) { this.formattedValue = formattedValue; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder targetValue(Double targetValue) { this.targetValue = targetValue; return this; }
        public Builder warningThreshold(Double warningThreshold) { this.warningThreshold = warningThreshold; return this; }
        public Builder criticalThreshold(Double criticalThreshold) { this.criticalThreshold = criticalThreshold; return this; }
        public Builder higherIsBetter(Boolean higherIsBetter) { this.higherIsBetter = higherIsBetter; return this; }
        public Builder status(KpiStatus status) { this.status = status; return this; }
        public Builder statusReason(String statusReason) { this.statusReason = statusReason; return this; }
        public Builder sampleCount(Long sampleCount) { this.sampleCount = sampleCount; return this; }
        public Builder historicalTrend(List<Double> historicalTrend) { this.historicalTrend = historicalTrend; return this; }

        public KpiCalculationResultResponse build() {
            return new KpiCalculationResultResponse(kpiId, kpiCode, name, description, category, value, formattedValue, unit, targetValue, warningThreshold, criticalThreshold, higherIsBetter, status, statusReason, sampleCount, historicalTrend);
        }
    }

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }
    public String getKpiCode() { return kpiCode; }
    public void setKpiCode(String kpiCode) { this.kpiCode = kpiCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public KpiCategory getCategory() { return category; }
    public void setCategory(KpiCategory category) { this.category = category; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getFormattedValue() { return formattedValue; }
    public void setFormattedValue(String formattedValue) { this.formattedValue = formattedValue; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
    public Double getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(Double warningThreshold) { this.warningThreshold = warningThreshold; }
    public Double getCriticalThreshold() { return criticalThreshold; }
    public void setCriticalThreshold(Double criticalThreshold) { this.criticalThreshold = criticalThreshold; }
    public Boolean getHigherIsBetter() { return higherIsBetter; }
    public void setHigherIsBetter(Boolean higherIsBetter) { this.higherIsBetter = higherIsBetter; }
    public KpiStatus getStatus() { return status; }
    public void setStatus(KpiStatus status) { this.status = status; }
    public String getStatusReason() { return statusReason; }
    public void setStatusReason(String statusReason) { this.statusReason = statusReason; }
    public Long getSampleCount() { return sampleCount; }
    public void setSampleCount(Long sampleCount) { this.sampleCount = sampleCount; }
    public List<Double> getHistoricalTrend() { return historicalTrend; }
    public void setHistoricalTrend(List<Double> historicalTrend) { this.historicalTrend = historicalTrend; }
}
