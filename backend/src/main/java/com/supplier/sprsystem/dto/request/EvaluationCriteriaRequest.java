package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EvaluationCriteriaRequest {

    @NotBlank(message = "Criteria name is required")
    @Size(max = 100, message = "Criteria name cannot exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Criteria code cannot exceed 50 characters")
    private String code;

    private String description;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight cannot be negative")
    @DecimalMax(value = "100.0", message = "Weight cannot exceed 100")
    private Double weight;

    @DecimalMin(value = "1.0", message = "Max score must be at least 1")
    @DecimalMax(value = "100.0", message = "Max score cannot exceed 100")
    private Double maxScore = 100.0;

    private Integer displayOrder = 1;

    private Boolean active = true;

    public EvaluationCriteriaRequest() {}

    public EvaluationCriteriaRequest(String name, String code, String description, Double weight, Double maxScore, Integer displayOrder, Boolean active) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.weight = weight;
        this.maxScore = maxScore != null ? maxScore : 100.0;
        this.displayOrder = displayOrder != null ? displayOrder : 1;
        this.active = active != null ? active : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String code;
        private String description;
        private Double weight;
        private Double maxScore = 100.0;
        private Integer displayOrder = 1;
        private Boolean active = true;

        public Builder name(String name) { this.name = name; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder weight(Double weight) { this.weight = weight; return this; }
        public Builder maxScore(Double maxScore) { this.maxScore = maxScore; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }

        public EvaluationCriteriaRequest build() {
            return new EvaluationCriteriaRequest(name, code, description, weight, maxScore, displayOrder, active);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
