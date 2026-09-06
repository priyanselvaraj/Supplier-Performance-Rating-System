package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.DocumentStatus;

import java.time.LocalDateTime;

public class SupplierDocumentResponse {

    private Long id;
    private Long supplierId;
    private String supplierName;
    private String documentName;
    private String documentType;
    private String contentType;
    private Long fileSize;
    private String uploadedByUsername;
    private DocumentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupplierDocumentResponse() {}

    public SupplierDocumentResponse(Long id, Long supplierId, String supplierName, String documentName, String documentType, String contentType, Long fileSize, String uploadedByUsername, DocumentStatus status, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.documentName = documentName;
        this.documentType = documentType;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadedByUsername = uploadedByUsername;
        this.status = status;
        this.notes = notes;
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
        private String documentName;
        private String documentType;
        private String contentType;
        private Long fileSize;
        private String uploadedByUsername;
        private DocumentStatus status;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder documentName(String documentName) { this.documentName = documentName; return this; }
        public Builder documentType(String documentType) { this.documentType = documentType; return this; }
        public Builder contentType(String contentType) { this.contentType = contentType; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public Builder uploadedByUsername(String uploadedByUsername) { this.uploadedByUsername = uploadedByUsername; return this; }
        public Builder status(DocumentStatus status) { this.status = status; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SupplierDocumentResponse build() {
            return new SupplierDocumentResponse(id, supplierId, supplierName, documentName, documentType, contentType, fileSize, uploadedByUsername, status, notes, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getUploadedByUsername() { return uploadedByUsername; }
    public void setUploadedByUsername(String uploadedByUsername) { this.uploadedByUsername = uploadedByUsername; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
