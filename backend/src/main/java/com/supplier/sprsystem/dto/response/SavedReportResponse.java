package com.supplier.sprsystem.dto.response;

import java.time.LocalDateTime;

public class SavedReportResponse {

    private Long id;
    private String name;
    private String description;
    private String reportType;
    private String scope;
    private String filters;
    private String selectedMetrics;
    private String visualization;
    private Boolean isPublic;
    private Long createdById;
    private String createdByUsername;
    private String createdByFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavedReportResponse() {}

    public SavedReportResponse(Long id, String name, String description, String reportType, String scope, String filters, String selectedMetrics, String visualization, Boolean isPublic, Long createdById, String createdByUsername, String createdByFullName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.reportType = reportType;
        this.scope = scope;
        this.filters = filters;
        this.selectedMetrics = selectedMetrics;
        this.visualization = visualization;
        this.isPublic = isPublic;
        this.createdById = createdById;
        this.createdByUsername = createdByUsername;
        this.createdByFullName = createdByFullName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String reportType;
        private String scope;
        private String filters;
        private String selectedMetrics;
        private String visualization;
        private Boolean isPublic;
        private Long createdById;
        private String createdByUsername;
        private String createdByFullName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder reportType(String reportType) { this.reportType = reportType; return this; }
        public Builder scope(String scope) { this.scope = scope; return this; }
        public Builder filters(String filters) { this.filters = filters; return this; }
        public Builder selectedMetrics(String selectedMetrics) { this.selectedMetrics = selectedMetrics; return this; }
        public Builder visualization(String visualization) { this.visualization = visualization; return this; }
        public Builder isPublic(Boolean isPublic) { this.isPublic = isPublic; return this; }
        public Builder createdById(Long createdById) { this.createdById = createdById; return this; }
        public Builder createdByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; return this; }
        public Builder createdByFullName(String createdByFullName) { this.createdByFullName = createdByFullName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SavedReportResponse build() {
            return new SavedReportResponse(id, name, description, reportType, scope, filters, selectedMetrics, visualization, isPublic, createdById, createdByUsername, createdByFullName, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    public String getCreatedByFullName() { return createdByFullName; }
    public void setCreatedByFullName(String createdByFullName) { this.createdByFullName = createdByFullName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
