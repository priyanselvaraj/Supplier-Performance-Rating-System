package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.UpdateRequestStatus;

import java.time.LocalDateTime;

public class SupplierProfileUpdateRequestDto {

    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long requestedByUserId;
    private String requestedByUsername;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String website;
    private String city;
    private String state;
    private String country;
    private UpdateRequestStatus status;
    private String reviewerNotes;
    private String reviewedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public SupplierProfileUpdateRequestDto() {}

    public SupplierProfileUpdateRequestDto(Long id, Long supplierId, String supplierName, Long requestedByUserId, String requestedByUsername, String contactPerson, String phone, String email, String address, String website, String city, String state, String country, UpdateRequestStatus status, String reviewerNotes, String reviewedByUsername, LocalDateTime createdAt, LocalDateTime reviewedAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.requestedByUserId = requestedByUserId;
        this.requestedByUsername = requestedByUsername;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.status = status;
        this.reviewerNotes = reviewerNotes;
        this.reviewedByUsername = reviewedByUsername;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long supplierId;
        private String supplierName;
        private Long requestedByUserId;
        private String requestedByUsername;
        private String contactPerson;
        private String phone;
        private String email;
        private String address;
        private String website;
        private String city;
        private String state;
        private String country;
        private UpdateRequestStatus status;
        private String reviewerNotes;
        private String reviewedByUsername;
        private LocalDateTime createdAt;
        private LocalDateTime reviewedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder requestedByUserId(Long requestedByUserId) { this.requestedByUserId = requestedByUserId; return this; }
        public Builder requestedByUsername(String requestedByUsername) { this.requestedByUsername = requestedByUsername; return this; }
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
        public Builder reviewedByUsername(String reviewedByUsername) { this.reviewedByUsername = reviewedByUsername; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }

        public SupplierProfileUpdateRequestDto build() {
            return new SupplierProfileUpdateRequestDto(id, supplierId, supplierName, requestedByUserId, requestedByUsername, contactPerson, phone, email, address, website, city, state, country, status, reviewerNotes, reviewedByUsername, createdAt, reviewedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getRequestedByUserId() { return requestedByUserId; }
    public void setRequestedByUserId(Long requestedByUserId) { this.requestedByUserId = requestedByUserId; }
    public String getRequestedByUsername() { return requestedByUsername; }
    public void setRequestedByUsername(String requestedByUsername) { this.requestedByUsername = requestedByUsername; }
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
    public String getReviewedByUsername() { return reviewedByUsername; }
    public void setReviewedByUsername(String reviewedByUsername) { this.reviewedByUsername = reviewedByUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
