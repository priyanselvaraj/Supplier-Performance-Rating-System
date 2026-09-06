package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Category code cannot exceed 50 characters")
    private String code;

    private String description;

    private Boolean active;

    public SupplierCategoryRequest() {}

    public SupplierCategoryRequest(String name, String code, String description, Boolean active) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.active = active != null ? active : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String code;
        private String description;
        private Boolean active = true;

        public Builder name(String name) { this.name = name; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }

        public SupplierCategoryRequest build() {
            return new SupplierCategoryRequest(name, code, description, active);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
