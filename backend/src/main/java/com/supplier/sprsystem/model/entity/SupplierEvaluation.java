package com.supplier.sprsystem.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_evaluations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_evaluations_code", columnNames = "evaluation_code")
        },
        indexes = {
                @Index(name = "idx_eval_supplier_id", columnList = "supplier_id"),
                @Index(name = "idx_eval_evaluator_id", columnList = "evaluator_id"),
                @Index(name = "idx_eval_date", columnList = "evaluation_date"),
                @Index(name = "idx_eval_status", columnList = "status"),
                @Index(name = "idx_eval_rating_cat", columnList = "rating_category"),
                @Index(name = "idx_eval_code", columnList = "evaluation_code")
        })
public class SupplierEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "evaluation_code", nullable = false, unique = true, length = 50)
    private String evaluationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @NotNull
    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Size(max = 50)
    @Column(name = "evaluation_period", length = 50)
    private String evaluationPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private EvaluationStatus status = EvaluationStatus.COMPLETED;

    @Column(name = "total_weighted_score", nullable = false)
    private Double totalWeightedScore = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category", length = 20, nullable = false)
    private RatingCategory ratingCategory = RatingCategory.UNRATED;

    @Column(name = "general_comments", columnDefinition = "TEXT")
    private String generalComments;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovement;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EvaluationScore> scores = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SupplierEvaluation() {}

    public SupplierEvaluation(Long id, String evaluationCode, Supplier supplier, User evaluator, LocalDate evaluationDate, String evaluationPeriod, EvaluationStatus status, Double totalWeightedScore, RatingCategory ratingCategory, String generalComments, String strengths, String areasForImprovement, String recommendation, List<EvaluationScore> scores, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.evaluationCode = evaluationCode;
        this.supplier = supplier;
        this.evaluator = evaluator;
        this.evaluationDate = evaluationDate;
        this.evaluationPeriod = evaluationPeriod;
        this.status = status != null ? status : EvaluationStatus.COMPLETED;
        this.totalWeightedScore = totalWeightedScore != null ? totalWeightedScore : 0.0;
        this.ratingCategory = ratingCategory != null ? ratingCategory : RatingCategory.UNRATED;
        this.generalComments = generalComments;
        this.strengths = strengths;
        this.areasForImprovement = areasForImprovement;
        this.recommendation = recommendation;
        this.scores = scores != null ? scores : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String evaluationCode;
        private Supplier supplier;
        private User evaluator;
        private LocalDate evaluationDate;
        private String evaluationPeriod;
        private EvaluationStatus status = EvaluationStatus.COMPLETED;
        private Double totalWeightedScore = 0.0;
        private RatingCategory ratingCategory = RatingCategory.UNRATED;
        private String generalComments;
        private String strengths;
        private String areasForImprovement;
        private String recommendation;
        private List<EvaluationScore> scores = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder evaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder evaluator(User evaluator) { this.evaluator = evaluator; return this; }
        public Builder evaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; return this; }
        public Builder evaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; return this; }
        public Builder status(EvaluationStatus status) { this.status = status; return this; }
        public Builder totalWeightedScore(Double totalWeightedScore) { this.totalWeightedScore = totalWeightedScore; return this; }
        public Builder totalScore(Double totalScore) { this.totalWeightedScore = totalScore; return this; }
        public Builder ratingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; return this; }
        public Builder generalComments(String generalComments) { this.generalComments = generalComments; return this; }
        public Builder comments(String comments) { this.generalComments = comments; return this; }
        public Builder strengths(String strengths) { this.strengths = strengths; return this; }
        public Builder areasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder scores(List<EvaluationScore> scores) { this.scores = scores; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SupplierEvaluation build() {
            return new SupplierEvaluation(id, evaluationCode, supplier, evaluator, evaluationDate, evaluationPeriod, status, totalWeightedScore, ratingCategory, generalComments, strengths, areasForImprovement, recommendation, scores, createdAt, updatedAt);
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

    public void addScore(EvaluationScore score) {
        scores.add(score);
        score.setEvaluation(this);
    }

    public void removeScore(EvaluationScore score) {
        scores.remove(score);
        score.setEvaluation(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEvaluationCode() { return evaluationCode; }
    public void setEvaluationCode(String evaluationCode) { this.evaluationCode = evaluationCode; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public User getEvaluator() { return evaluator; }
    public void setEvaluator(User evaluator) { this.evaluator = evaluator; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public String getEvaluationPeriod() { return evaluationPeriod; }
    public void setEvaluationPeriod(String evaluationPeriod) { this.evaluationPeriod = evaluationPeriod; }
    public EvaluationStatus getStatus() { return status; }
    public void setStatus(EvaluationStatus status) { this.status = status; }
    public Double getTotalWeightedScore() { return totalWeightedScore; }
    public void setTotalWeightedScore(Double totalWeightedScore) { this.totalWeightedScore = totalWeightedScore; }
    public Double getTotalScore() { return totalWeightedScore; }
    public void setTotalScore(Double totalScore) { this.totalWeightedScore = totalScore; }
    public RatingCategory getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(RatingCategory ratingCategory) { this.ratingCategory = ratingCategory; }
    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }
    public String getComments() { return generalComments; }
    public void setComments(String comments) { this.generalComments = comments; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public List<EvaluationScore> getScores() { return scores; }
    public void setScores(List<EvaluationScore> scores) { this.scores = scores; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
