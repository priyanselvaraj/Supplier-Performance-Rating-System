package com.supplier.sprsystem.dto.response;

import java.time.LocalDateTime;

public class SupplierCommunicationResponse {

    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long senderId;
    private String senderName;
    private String subject;
    private String message;
    private String relatedResourceType;
    private Long relatedResourceId;
    private boolean fromSupplier;
    private LocalDateTime createdAt;

    public SupplierCommunicationResponse() {}

    public SupplierCommunicationResponse(Long id, Long supplierId, String supplierName, Long senderId, String senderName, String subject, String message, String relatedResourceType, Long relatedResourceId, boolean fromSupplier, LocalDateTime createdAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.subject = subject;
        this.message = message;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.fromSupplier = fromSupplier;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long supplierId;
        private String supplierName;
        private Long senderId;
        private String senderName;
        private String subject;
        private String message;
        private String relatedResourceType;
        private Long relatedResourceId;
        private boolean fromSupplier = true;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder senderId(Long senderId) { this.senderId = senderId; return this; }
        public Builder senderName(String senderName) { this.senderName = senderName; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder relatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; return this; }
        public Builder relatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; return this; }
        public Builder fromSupplier(boolean fromSupplier) { this.fromSupplier = fromSupplier; return this; }
        public Builder isFromSupplier(boolean isFromSupplier) { this.fromSupplier = isFromSupplier; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SupplierCommunicationResponse build() {
            return new SupplierCommunicationResponse(id, supplierId, supplierName, senderId, senderName, subject, message, relatedResourceType, relatedResourceId, fromSupplier, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
    public boolean isFromSupplier() { return fromSupplier; }
    public void setFromSupplier(boolean fromSupplier) { this.fromSupplier = fromSupplier; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
