package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_reports")
public class SavedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Column(name = "scope", length = 50)
    private String scope = "ALL_SUPPLIERS";

    @Column(name = "filters", columnDefinition = "TEXT")
    private String filters;

    @Column(name = "selected_metrics", columnDefinition = "TEXT")
    private String selectedMetrics;

    @Column(name = "visualization", length = 30)
    private String visualization = "TABLE";

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SavedReport() {}

    public SavedReport(Long id, String name, String description, String reportType, String scope, String filters, String selectedMetrics, String visualization, Boolean isPublic, User createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.reportType = reportType;
        this.scope = scope != null ? scope : "ALL_SUPPLIERS";
        this.filters = filters;
        this.selectedMetrics = selectedMetrics;
        this.visualization = visualization != null ? visualization : "TABLE";
        this.isPublic = isPublic != null ? isPublic : false;
        this.createdBy = createdBy;
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
        private String scope = "ALL_SUPPLIERS";
        private String filters;
        private String selectedMetrics;
        private String visualization = "TABLE";
        private Boolean isPublic = false;
        private User createdBy;
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
        public Builder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SavedReport build() {
            return new SavedReport(id, name, description, reportType, scope, filters, selectedMetrics, visualization, isPublic, createdBy, createdAt, updatedAt);
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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
