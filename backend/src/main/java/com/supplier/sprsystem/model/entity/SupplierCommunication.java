package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_communications",
        indexes = {
                @Index(name = "idx_comm_supplier", columnList = "supplier_id"),
                @Index(name = "idx_comm_created", columnList = "created_at")
        })
public class SupplierCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String subject;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Size(max = 50)
    @Column(name = "related_resource_type", length = 50)
    private String relatedResourceType;

    @Column(name = "related_resource_id")
    private Long relatedResourceId;

    @Column(name = "is_from_supplier", nullable = false)
    private boolean fromSupplier = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SupplierCommunication() {}

    public SupplierCommunication(Long id, Supplier supplier, User sender, String subject, String message, String relatedResourceType, Long relatedResourceId, boolean fromSupplier, LocalDateTime createdAt) {
        this.id = id;
        this.supplier = supplier;
        this.sender = sender;
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
        private Supplier supplier;
        private User sender;
        private String subject;
        private String message;
        private String relatedResourceType;
        private Long relatedResourceId;
        private boolean fromSupplier = true;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder sender(User sender) { this.sender = sender; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder relatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; return this; }
        public Builder relatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; return this; }
        public Builder fromSupplier(boolean fromSupplier) { this.fromSupplier = fromSupplier; return this; }
        public Builder isFromSupplier(boolean isFromSupplier) { this.fromSupplier = isFromSupplier; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SupplierCommunication build() {
            return new SupplierCommunication(id, supplier, sender, subject, message, relatedResourceType, relatedResourceId, fromSupplier, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
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
