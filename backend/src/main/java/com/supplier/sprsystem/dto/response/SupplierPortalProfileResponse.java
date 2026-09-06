package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.RatingCategory;
import com.supplier.sprsystem.model.entity.SupplierStatus;

import java.time.LocalDateTime;
import java.util.List;

public class SupplierPortalProfileResponse {

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
    private Long categoryId;
    private String categoryName;
    private SupplierStatus status;
    private Double overallRating;
    private RatingCategory ratingCategory;
    private Integer totalEvaluations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean hasPendingUpdateRequest;
    private List<SupplierProfileUpdateRequestDto> updateRequests;

    public SupplierPortalProfileResponse() {}

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
        private Long categoryId;
        private String categoryName;
        private SupplierStatus status;
        private Double overallRating;
        private RatingCategory ratingCategory;
        private Integer totalEvaluations;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean hasPendingUpdateRequest;
        private List<SupplierProfileUpdateRequestDto> updateRequests;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplierCode(String supplierCode) { this.supplierCode = supplierCode; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder contactPerson(String contactPerson) { this.contactPerson = contactPerson; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder status(SupplierStatus status) { this.status = status; return this; }
        public Builder overallRating(Double overallRating) { this.overallRating = overallRating; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder hasPendingUpdateRequest(boolean hasPendingUpdateRequest) { this.hasPendingUpdateRequest = hasPendingUpdateRequest; return this; }
        public Builder updateRequests(List<SupplierProfileUpdateRequestDto> updateRequests) { this.updateRequests = updateRequests; return this; }

        public SupplierPortalProfileResponse build() {
            SupplierPortalProfileResponse r = new SupplierPortalProfileResponse();
            r.id = this.id;
            r.supplierCode = this.supplierCode;
            r.name = this.name;
            r.contactPerson = this.contactPerson;
            r.email = this.email;
            r.phone = this.phone;
            r.address = this.address;
            r.website = this.website;
            r.city = this.city;
            r.state = this.state;
            r.country = this.country;
            r.categoryId = this.categoryId;
            r.categoryName = this.categoryName;
            r.status = this.status;
            r.overallRating = this.overallRating;
            r.ratingCategory = this.ratingCategory;
            r.totalEvaluations = this.totalEvaluations;
            r.createdAt = this.createdAt;
            r.updatedAt = this.updatedAt;
            r.hasPendingUpdateRequest = this.hasPendingUpdateRequest;
            r.updateRequests = this.updateRequests;
            return r;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
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
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public SupplierStatus getStatus() { return status; }
    public void setStatus(SupplierStatus status) { this.status = status; }
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
    public boolean isHasPendingUpdateRequest() { return hasPendingUpdateRequest; }
    public void setHasPendingUpdateRequest(boolean hasPendingUpdateRequest) { this.hasPendingUpdateRequest = hasPendingUpdateRequest; }
    public List<SupplierProfileUpdateRequestDto> getUpdateRequests() { return updateRequests; }
    public void setUpdateRequests(List<SupplierProfileUpdateRequestDto> updateRequests) { this.updateRequests = updateRequests; }
}
