package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kpi_definitions")
public class KpiDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kpi_code", nullable = false, unique = true, length = 50)
    private String kpiCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private KpiCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 30)
    private KpiCalculationType calculationType;

    @Column(name = "unit", length = 20)
    private String unit = "POINTS";

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "warning_threshold")
    private Double warningThreshold;

    @Column(name = "critical_threshold")
    private Double criticalThreshold;

    @Column(name = "higher_is_better", nullable = false)
    private Boolean higherIsBetter = true;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public KpiDefinition() {}

    public KpiDefinition(Long id, String kpiCode, String name, String description, KpiCategory category, KpiCalculationType calculationType, String unit, Double targetValue, Double warningThreshold, Double criticalThreshold, Boolean higherIsBetter, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.kpiCode = kpiCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.calculationType = calculationType;
        this.unit = unit != null ? unit : "POINTS";
        this.targetValue = targetValue;
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;
        this.higherIsBetter = higherIsBetter != null ? higherIsBetter : true;
        this.active = active != null ? active : true;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String kpiCode;
        private String name;
        private String description;
        private KpiCategory category;
        private KpiCalculationType calculationType;
        private String unit = "POINTS";
        private Double targetValue;
        private Double warningThreshold;
        private Double criticalThreshold;
        private Boolean higherIsBetter = true;
        private Boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
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
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public KpiDefinition build() {
            return new KpiDefinition(id, kpiCode, name, description, category, calculationType, unit, targetValue, warningThreshold, criticalThreshold, higherIsBetter, active, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
