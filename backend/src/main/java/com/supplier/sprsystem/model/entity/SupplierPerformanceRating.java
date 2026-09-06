package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_performance_ratings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_spr_evaluation_id", columnNames = "evaluation_id")
        },
        indexes = {
                @Index(name = "idx_spr_supplier_id", columnList = "supplier_id"),
                @Index(name = "idx_spr_rating_date", columnList = "rating_date"),
                @Index(name = "idx_spr_rating", columnList = "rating"),
                @Index(name = "idx_spr_perf_status", columnList = "performance_status")
        })
public class SupplierPerformanceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_id", nullable = false, unique = true)
    private SupplierEvaluation evaluation;

    @NotNull
    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", length = 30, nullable = false)
    private SupplierRating rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "performance_status", length = 30, nullable = false)
    private PerformanceStatus performanceStatus;

    @NotNull
    @Column(name = "rating_date", nullable = false)
    private LocalDate ratingDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SupplierPerformanceRating() {}

    public SupplierPerformanceRating(Long id, Supplier supplier, SupplierEvaluation evaluation, Double score, SupplierRating rating, PerformanceStatus performanceStatus, LocalDate ratingDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplier = supplier;
        this.evaluation = evaluation;
        this.score = score;
        this.rating = rating;
        this.performanceStatus = performanceStatus;
        this.ratingDate = ratingDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Supplier supplier;
        private SupplierEvaluation evaluation;
        private Double score;
        private SupplierRating rating;
        private PerformanceStatus performanceStatus;
        private LocalDate ratingDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder evaluation(SupplierEvaluation evaluation) { this.evaluation = evaluation; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder rating(SupplierRating rating) { this.rating = rating; return this; }
        public Builder performanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; return this; }
        public Builder ratingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SupplierPerformanceRating build() {
            return new SupplierPerformanceRating(id, supplier, evaluation, score, rating, performanceStatus, ratingDate, createdAt, updatedAt);
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
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public SupplierEvaluation getEvaluation() { return evaluation; }
    public void setEvaluation(SupplierEvaluation evaluation) { this.evaluation = evaluation; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public SupplierRating getRating() { return rating; }
    public void setRating(SupplierRating rating) { this.rating = rating; }
    public PerformanceStatus getPerformanceStatus() { return performanceStatus; }
    public void setPerformanceStatus(PerformanceStatus performanceStatus) { this.performanceStatus = performanceStatus; }
    public LocalDate getRatingDate() { return ratingDate; }
    public void setRatingDate(LocalDate ratingDate) { this.ratingDate = ratingDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
