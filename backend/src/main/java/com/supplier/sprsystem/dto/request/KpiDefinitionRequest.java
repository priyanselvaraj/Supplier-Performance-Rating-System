package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.KpiCategory;
import com.supplier.sprsystem.model.entity.KpiCalculationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class KpiDefinitionRequest {

    @NotBlank(message = "KPI code is required")
    @Size(max = 50, message = "KPI code must not exceed 50 characters")
    private String kpiCode;

    @NotBlank(message = "KPI name is required")
    @Size(max = 100, message = "KPI name must not exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "KPI category is required")
    private KpiCategory category;

    @NotNull(message = "Calculation type is required")
    private KpiCalculationType calculationType;

    private String unit;
    private Double targetValue;
    private Double warningThreshold;
    private Double criticalThreshold;
    private Boolean higherIsBetter;
    private Boolean active;

    public KpiDefinitionRequest() {}

    public KpiDefinitionRequest(String kpiCode, String name, String description, KpiCategory category, KpiCalculationType calculationType, String unit, Double targetValue, Double warningThreshold, Double criticalThreshold, Boolean higherIsBetter, Boolean active) {
        this.kpiCode = kpiCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.calculationType = calculationType;
        this.unit = unit;
        this.targetValue = targetValue;
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;
        this.higherIsBetter = higherIsBetter;
        this.active = active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String kpiCode;
        private String name;
        private String description;
        private KpiCategory category;
        private KpiCalculationType calculationType;
        private String unit;
        private Double targetValue;
        private Double warningThreshold;
        private Double criticalThreshold;
        private Boolean higherIsBetter;
        private Boolean active;

        public Builder kpiCode(String kpiCode) { this.kpiCode = kpiCode; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(KpiCategory category) { this.category = category; return this; }
        public Builder calculationType(KpiCalculationType calculationType) { this.calculationType = calculationType; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder targetValue(Double targetValue) { this.targetValue = targetValue; return this; }
        public Builder warningThreshold(Double warningThreshold) { this.warningThreshold = warningThreshold; return this; }
        public Builder criticalThreshold(Double criticalThreshold) { this.criticalThreshold = criticalThreshold; return this; }
        public Builder higherIsBetter(Boolean higherIsBetter) { this.higherIsBetter = higherIsBetter; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }

        public KpiDefinitionRequest build() {
            return new KpiDefinitionRequest(kpiCode, name, description, category, calculationType, unit, targetValue, warningThreshold, criticalThreshold, higherIsBetter, active);
        }
    }

    public String getKpiCode() { return kpiCode; }
    public void setKpiCode(String kpiCode) { this.kpiCode = kpiCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public KpiCategory getCategory() { return category; }
    public void setCategory(KpiCategory category) { this.category = category; }
    public KpiCalculationType getCalculationType() { return calculationType; }
    public void setCalculationType(KpiCalculationType calculationType) { this.calculationType = calculationType; }
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
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
