package com.supplier.sprsystem.dto.integration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierSyncItem {

    @NotBlank(message = "Supplier code is required")
    @Size(max = 50)
    private String supplierCode;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 100)
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 25)
    private String phone;

    private String address;

    @Size(max = 150)
    private String website;

    private String categoryName;

    private String status = "ACTIVE";

    public SupplierSyncItem() {
    }

    public SupplierSyncItem(String supplierCode, String name, String contactPerson,
                            String email, String phone, String address,
                            String website, String categoryName, String status) {
        this.supplierCode = supplierCode;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.website = website;
        this.categoryName = categoryName;
        this.status = status != null ? status : "ACTIVE";
    }

    // Getters and Setters
    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
