package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class SupplierProfileUpdateSubmitRequest {

    @Size(max = 100)
    private String contactPerson;

    @Size(max = 25)
    private String phone;

    @Email
    @Size(max = 100)
    private String email;

    private String address;

    @Size(max = 150)
    private String website;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    public SupplierProfileUpdateSubmitRequest() {}

    public SupplierProfileUpdateSubmitRequest(String contactPerson, String phone, String email, String address, String website, String city, String state, String country) {
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
    }

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
}
