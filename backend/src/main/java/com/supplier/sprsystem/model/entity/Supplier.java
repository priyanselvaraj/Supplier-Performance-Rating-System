package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_suppliers_code", columnNames = "supplier_code"),
                @UniqueConstraint(name = "uk_suppliers_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_suppliers_category_id", columnList = "category_id"),
                @Index(name = "idx_suppliers_status", columnList = "status"),
                @Index(name = "idx_suppliers_rating_cat", columnList = "rating_category"),
                @Index(name = "idx_suppliers_name", columnList = "name")
        })
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "supplier_code", nullable = false, unique = true, length = 50)
    private String supplierCode;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 100)
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @Size(max = 25)
    @Column(length = 25)
    private String phone;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private SupplierCategory category;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SupplierStatus status = SupplierStatus.ACTIVE;

    @Column(name = "overall_rating")
    private Double overallRating = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category", length = 20, nullable = false)
    private RatingCategory ratingCategory = RatingCategory.UNRATED;

    @Column(name = "total_evaluations", nullable = false)
    private Integer totalEvaluations = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Supplier() {}

    public Supplier(Long id, String supplierCode, String name, String contactPerson, String email, String phone, String address, String website, String city, String state, String country, SupplierCategory category, SupplierStatus status, Double overallRating, RatingCategory ratingCategory, Integer totalEvaluations, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplierCode = supplierCode;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.website = website;
        this.city = city;
        this.state = state;
        this.country = country;
        this.category = category;
        this.status = status != null ? status : SupplierStatus.ACTIVE;
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
        private SupplierCategory category;
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
        public Builder category(SupplierCategory category) { this.category = category; return this; }
        public Builder status(SupplierStatus status) { this.status = status; return this; }
        public Builder active(boolean active) { this.status = active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE; return this; }
        public Builder overallRating(Double overallRating) { this.overallRating = overallRating; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder totalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Supplier build() {
            return new Supplier(id, supplierCode, name, contactPerson, email, phone, address, website, city, state, country, category, status, overallRating, ratingCategory, totalEvaluations, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSupplierName() { return name; }
    public void setSupplierName(String supplierName) { this.name = supplierName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneNumber() { return phone; }
    public void setPhoneNumber(String phoneNumber) { this.phone = phoneNumber; }
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
    public SupplierCategory getCategory() { return category; }
    public void setCategory(SupplierCategory category) { this.category = category; }
    public SupplierStatus getStatus() { return status; }
    public void setStatus(SupplierStatus status) { this.status = status; }
    public boolean isActive() { return this.status == SupplierStatus.ACTIVE; }
    public void setActive(boolean active) { this.status = active ? SupplierStatus.ACTIVE : SupplierStatus.INACTIVE; }
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
