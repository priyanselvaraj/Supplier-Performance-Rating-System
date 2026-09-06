package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;

import java.time.LocalDateTime;

public class SupplierResponse {

    private Long id;
    private String supplierCode;
    private String name;
    private String supplierName;
    private String contactPerson;
    private String email;
    private String phone;
    private String phoneNumber;
    private String address;
    private String website;
    private String city;
    private String state;
    private String country;
    private SupplierCategoryResponse category;
    private SupplierStatus status = SupplierStatus.ACTIVE;
    private boolean active = true;
    private Double overallRating = 0.0;
    private RatingCategory ratingCategory = RatingCategory.UNRATED;
    private Integer totalEvaluations = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupplierResponse() {}

    public SupplierResponse(Long id, String supplierCode, String name, String contactPerson, String email, String phone, String address, String website, String city, String state, String country, SupplierCategoryResponse category, SupplierStatus status, Double overallRating, RatingCategory ratingCategory, Integer totalEvaluations, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplierCode = supplierCode;
        this.name = name;
        this.supplierName = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.phoneNumber = phone;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.category = category;
        this.status = status != null ? status : SupplierStatus.ACTIVE;
        this.active = this.status == SupplierStatus.ACTIVE;
        this.overallRating = overallRating != null ? overallRating : 0.0;
        this.ratingCategory = ratingCategory != null ? ratingCategory : RatingCategory.UNRATED;
        this.totalEvaluations = totalEvaluations != null ? totalEvaluations : 0;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String supplierCode;
        private String name;
        private String contactPerson;
        private String email;
        private String phone;
        private String address;
        private String website;
        private String city;
        private String state;
        private String country;
        private SupplierCategoryResponse category;
        private SupplierStatus status = SupplierStatus.ACTIVE;
        private Double overallRating = 0.0;
        private RatingCategory ratingCategory = RatingCategory.UNRATED;
        private Integer totalEvaluations = 0;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder supplierName(String supplierName) { this.name = supplierName; return this; }
        public Builder contactPerson(String contactPerson) { this.contactPerson = contactPerson; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phone = phoneNumber; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder category(SupplierCategoryResponse category) { this.category = category; return this; }
        public Builder status(SupplierStatus status) { this.status = status; return this; }
        public Builder active(boolean active) { this.status = active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE; return this; }
        public Builder overallRating(Double overallRating) { this.overallRating = overallRating; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SupplierResponse build() {
            return new SupplierResponse(id, supplierCode, name, contactPerson, email, phone, address, website, city, state, country, category, status, overallRating, ratingCategory, totalEvaluations, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.supplierName = name;
    }
    public String getSupplierName() { return name; }
    public void setSupplierName(String supplierName) {
        this.name = supplierName;
        this.supplierName = supplierName;
    }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneNumber = phone;
    }
    public String getPhoneNumber() { return phone; }
    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
        this.phoneNumber = phoneNumber;
    }
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
    public SupplierCategoryResponse getCategory() { return category; }
    public void setCategory(SupplierCategoryResponse category) { this.category = category; }
    public SupplierStatus getStatus() { return status; }
    public void setStatus(SupplierStatus status) {
        this.status = status;
        this.active = status == SupplierStatus.ACTIVE;
    }
    public boolean isActive() { return status == SupplierStatus.ACTIVE; }
    public void setActive(boolean active) {
        this.active = active;
        this.status = active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE;
    }
    public Double getOverallRating() { return overallRating; }
    public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public Integer getTotalEvaluations() { return totalEvaluations; }
    public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
