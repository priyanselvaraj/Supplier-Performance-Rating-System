package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SavedReportRequest {

    @NotBlank(message = "Report name is required")
    @Size(max = 150, message = "Report name must not exceed 150 characters")
    private String name;

    private String description;

    @NotBlank(message = "Report type is required")
    private String reportType;

    private String scope;
    private String filters;
    private String selectedMetrics;
    private String visualization;
    private Boolean isPublic;

    public SavedReportRequest() {}

    public SavedReportRequest(String name, String description, String reportType, String scope, String filters, String selectedMetrics, String visualization, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.reportType = reportType;
        this.scope = scope;
        this.filters = filters;
        this.selectedMetrics = selectedMetrics;
        this.visualization = visualization;
        this.isPublic = isPublic;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private String reportType;
        private String scope;
        private String filters;
        private String selectedMetrics;
        private String visualization;
        private Boolean isPublic;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder reportType(String reportType) { this.reportType = reportType; return this; }
        public Builder scope(String scope) { this.scope = scope; return this; }
        public Builder filters(String filters) { this.filters = filters; return this; }
        public Builder selectedMetrics(String selectedMetrics) { this.selectedMetrics = selectedMetrics; return this; }
        public Builder visualization(String visualization) { this.visualization = visualization; return this; }
        public Builder isPublic(Boolean isPublic) { this.isPublic = isPublic; return this; }

        public SavedReportRequest build() {
            return new SavedReportRequest(name, description, reportType, scope, filters, selectedMetrics, visualization, isPublic);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }
    public String getSelectedMetrics() { return selectedMetrics; }
    public void setSelectedMetrics(String selectedMetrics) { this.selectedMetrics = selectedMetrics; }
    public String getVisualization() { return visualization; }
    public void setVisualization(String visualization) { this.visualization = visualization; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
