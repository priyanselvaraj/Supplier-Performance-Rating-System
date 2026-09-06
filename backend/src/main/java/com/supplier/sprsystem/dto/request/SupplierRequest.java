package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.SupplierStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SupplierRequest {

    private String name;
    private String supplierName;

    @Size(max = 100, message = "Contact person cannot exceed 100 characters")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    private String phone;
    private String phoneNumber;

    private String address;

    @Size(max = 150, message = "Website cannot exceed 150 characters")
    private String website;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @NotNull(message = "Supplier category ID is required")
    private Long categoryId;

    private Boolean active;
    private SupplierStatus status;

    public SupplierRequest() {}

    public SupplierRequest(String name, String supplierName, String contactPerson, String email, String phone, String phoneNumber, String address, String website, String city, String state, String country, Long categoryId, Boolean active, SupplierStatus status) {
        this.name = name != null ? name : supplierName;
        this.supplierName = this.name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone != null ? phone : phoneNumber;
        this.phoneNumber = this.phone;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.categoryId = categoryId;
        this.active = active != null ? active : true;
        this.status = status != null ? status : (this.active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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
        private Long categoryId;
        private Boolean active = true;
        private SupplierStatus status = SupplierStatus.ACTIVE;

        public Builder name(String name) {
            this.name = name;
            this.supplierName = name;
            return this;
        }
        public Builder supplierName(String supplierName) {
            this.supplierName = supplierName;
            this.name = supplierName;
            return this;
        }
        public Builder contactPerson(String contactPerson) { this.contactPerson = contactPerson; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) {
            this.phone = phone;
            this.phoneNumber = phone;
            return this;
        }
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            this.phone = phoneNumber;
            return this;
        }
        public Builder address(String address) { this.address = address; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }
        public Builder status(SupplierStatus status) { this.status = status; return this; }

        public SupplierRequest build() {
            return new SupplierRequest(name, supplierName, contactPerson, email, phone, phoneNumber, address, website, city, state, country, categoryId, active, status);
        }
    }

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150, message = "Supplier name cannot exceed 150 characters")
    public String getName() {
        return name != null && !name.trim().isEmpty() ? name.trim() : (supplierName != null ? supplierName.trim() : null);
    }

    public void setName(String name) {
        this.name = name;
        this.supplierName = name;
    }

    public String getSupplierName() {
        return getName();
    }

    public void setSupplierName(String supplierName) {
        setName(supplierName);
    }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Size(max = 25, message = "Phone number cannot exceed 25 characters")
    public String getPhone() {
        return phone != null && !phone.trim().isEmpty() ? phone.trim() : (phoneNumber != null ? phoneNumber.trim() : null);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneNumber = phone;
    }

    public String getPhoneNumber() {
        return getPhone();
    }

    public void setPhoneNumber(String phoneNumber) {
        setPhone(phoneNumber);
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
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public SupplierStatus getStatus() { return status; }
    public void setStatus(SupplierStatus status) { this.status = status; }
}
