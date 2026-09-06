package com.supplier.sprsystem.dto.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportBuilderRequest {

    private String reportTitle;
    private String reportScope;
    private Long categoryId;
    private List<Long> supplierIds = new ArrayList<>();
    private String status;
    private String ratingCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> selectedMetrics = new ArrayList<>();
    private String groupBy;
    private String visualization;
    private Map<String, Object> customFilters = new HashMap<>();

    public ReportBuilderRequest() {}

    public ReportBuilderRequest(String reportTitle, String reportScope, Long categoryId, List<Long> supplierIds, String status, String ratingCategory, LocalDate startDate, LocalDate endDate, List<String> selectedMetrics, String groupBy, String visualization, Map<String, Object> customFilters) {
        this.reportTitle = reportTitle;
        this.reportScope = reportScope;
        this.categoryId = categoryId;
        this.supplierIds = supplierIds != null ? supplierIds : new ArrayList<>();
        this.status = status;
        this.ratingCategory = ratingCategory;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedMetrics = selectedMetrics != null ? selectedMetrics : new ArrayList<>();
        this.groupBy = groupBy;
        this.visualization = visualization;
        this.customFilters = customFilters != null ? customFilters : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reportTitle;
        private String reportScope;
        private Long categoryId;
        private List<Long> supplierIds = new ArrayList<>();
        private String status;
        private String ratingCategory;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> selectedMetrics = new ArrayList<>();
        private String groupBy;
        private String visualization;
        private Map<String, Object> customFilters = new HashMap<>();

        public Builder reportTitle(String reportTitle) { this.reportTitle = reportTitle; return this; }
        public Builder reportScope(String reportScope) { this.reportScope = reportScope; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder supplierIds(List<Long> supplierIds) { this.supplierIds = supplierIds; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder ratingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder selectedMetrics(List<String> selectedMetrics) { this.selectedMetrics = selectedMetrics; return this; }
        public Builder groupBy(String groupBy) { this.groupBy = groupBy; return this; }
        public Builder visualization(String visualization) { this.visualization = visualization; return this; }
        public Builder customFilters(Map<String, Object> customFilters) { this.customFilters = customFilters; return this; }

        public ReportBuilderRequest build() {
            return new ReportBuilderRequest(reportTitle, reportScope, categoryId, supplierIds, status, ratingCategory, startDate, endDate, selectedMetrics, groupBy, visualization, customFilters);
        }
    }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getReportScope() { return reportScope; }
    public void setReportScope(String reportScope) { this.reportScope = reportScope; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<Long> getSupplierIds() { return supplierIds; }
    public void setSupplierIds(List<Long> supplierIds) { this.supplierIds = supplierIds; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public List<String> getSelectedMetrics() { return selectedMetrics; }
    public void setSelectedMetrics(List<String> selectedMetrics) { this.selectedMetrics = selectedMetrics; }
    public String getGroupBy() { return groupBy; }
    public void setGroupBy(String groupBy) { this.groupBy = groupBy; }
    public String getVisualization() { return visualization; }
    public void setVisualization(String visualization) { this.visualization = visualization; }
    public Map<String, Object> getCustomFilters() { return customFilters; }
    public void setCustomFilters(Map<String, Object> customFilters) { this.customFilters = customFilters; }
}
