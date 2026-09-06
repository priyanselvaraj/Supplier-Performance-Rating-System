package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.ImprovementActionPriority;
import com.supplier.sprsystem.model.entity.ImprovementActionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ImprovementActionResponse {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private String supplierCode;
    private String title;
    private String description;
    private ImprovementActionPriority priority;
    private ImprovementActionStatus status;
    private Long assignedUserId;
    private String assignedUserName;
    private Long createdByUserId;
    private String createdByUserName;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ImprovementActionResponse() {}

    public ImprovementActionResponse(Long id, Long supplierId, String supplierName, String supplierCode, String title, String description, ImprovementActionPriority priority, ImprovementActionStatus status, Long assignedUserId, String assignedUserName, Long createdByUserId, String createdByUserName, LocalDate dueDate, LocalDateTime completedAt, String resolutionNotes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierCode = supplierCode;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedUserId = assignedUserId;
        this.assignedUserName = assignedUserName;
        this.createdByUserId = createdByUserId;
        this.createdByUserName = createdByUserName;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long supplierId;
        private String supplierName;
        private String supplierCode;
        private String title;
        private String description;
        private ImprovementActionPriority priority;
        private ImprovementActionStatus status;
        private Long assignedUserId;
        private String assignedUserName;
        private Long createdByUserId;
        private String createdByUserName;
        private LocalDate dueDate;
        private LocalDateTime completedAt;
        private String resolutionNotes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierId(Long id) { this.supplierId = id; return this; }
        public Builder supplierName(String name) { this.supplierName = name; return this; }
        public Builder supplierCode(String code) { this.supplierCode = code; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder priority(ImprovementActionPriority p) { this.priority = p; return this; }
        public Builder status(ImprovementActionStatus s) { this.status = s; return this; }
        public Builder assignedUserId(Long id) { this.assignedUserId = id; return this; }
        public Builder assignedUserName(String name) { this.assignedUserName = name; return this; }
        public Builder createdByUserId(Long id) { this.createdByUserId = id; return this; }
        public Builder createdByUserName(String name) { this.createdByUserName = name; return this; }
        public Builder dueDate(LocalDate date) { this.dueDate = date; return this; }
        public Builder completedAt(LocalDateTime dt) { this.completedAt = dt; return this; }
        public Builder resolutionNotes(String notes) { this.resolutionNotes = notes; return this; }
        public Builder createdAt(LocalDateTime dt) { this.createdAt = dt; return this; }
        public Builder updatedAt(LocalDateTime dt) { this.updatedAt = dt; return this; }

        public ImprovementActionResponse build() {
            return new ImprovementActionResponse(id, supplierId, supplierName, supplierCode, title, description, priority, status, assignedUserId, assignedUserName, createdByUserId, createdByUserName, dueDate, completedAt, resolutionNotes, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ImprovementActionPriority getPriority() { return priority; }
    public void setPriority(ImprovementActionPriority priority) { this.priority = priority; }
    public ImprovementActionStatus getStatus() { return status; }
    public void setStatus(ImprovementActionStatus status) { this.status = status; }
    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }
    public String getAssignedUserName() { return assignedUserName; }
    public void setAssignedUserName(String assignedUserName) { this.assignedUserName = assignedUserName; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }
    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
