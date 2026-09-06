package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_profile_update_requests",
        indexes = {
                @Index(name = "idx_profile_req_supplier", columnList = "supplier_id"),
                @Index(name = "idx_profile_req_status", columnList = "status")
        })
public class SupplierProfileUpdateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @Size(max = 100)
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Size(max = 25)
    @Column(length = 25)
    private String phone;

    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Size(max = 150)
    @Column(length = 150)
    private String website;

    @Size(max = 100)
    @Column(length = 100)
    private String city;

    @Size(max = 100)
    @Column(length = 100)
    private String state;

    @Size(max = 100)
    @Column(length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private UpdateRequestStatus status = UpdateRequestStatus.PENDING;

    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    private String reviewerNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public SupplierProfileUpdateRequest() {}

    public SupplierProfileUpdateRequest(Long id, Supplier supplier, User requestedBy, String contactPerson, String phone, String email, String address, String website, String city, String state, String country, UpdateRequestStatus status, String reviewerNotes, User reviewedBy, LocalDateTime createdAt, LocalDateTime reviewedAt) {
        this.id = id;
        this.supplier = supplier;
        this.requestedBy = requestedBy;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.status = status != null ? status : UpdateRequestStatus.PENDING;
        this.reviewerNotes = reviewerNotes;
        this.reviewedBy = reviewedBy;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Supplier supplier;
        private User requestedBy;
        private String contactPerson;
        private String phone;
        private String email;
        private String address;
        private String website;
        private String city;
        private String state;
        private String country;
        private UpdateRequestStatus status = UpdateRequestStatus.PENDING;
        private String reviewerNotes;
        private User reviewedBy;
        private LocalDateTime createdAt;
        private LocalDateTime reviewedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder requestedBy(User requestedBy) { this.requestedBy = requestedBy; return this; }
        public Builder contactPerson(String contactPerson) { this.contactPerson = contactPerson; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder status(UpdateRequestStatus status) { this.status = status; return this; }
        public Builder reviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; return this; }
        public Builder reviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }

        public SupplierProfileUpdateRequest build() {
            return new SupplierProfileUpdateRequest(id, supplier, requestedBy, contactPerson, phone, email, address, website, city, state, country, status, reviewerNotes, reviewedBy, createdAt, reviewedAt);
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
    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public UpdateRequestStatus getStatus() { return status; }
    public void setStatus(UpdateRequestStatus status) { this.status = status; }
    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }
    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
