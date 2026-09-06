package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendation_decisions")
public class AiRecommendationDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "recommendation_ref", length = 100)
    private String recommendationRef;

    @Column(name = "criterion_name", length = 100)
    private String criterionName;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 30)
    private RecommendationPriority priority = RecommendationPriority.MEDIUM;

    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;

    @Column(name = "actionable_steps", columnDefinition = "TEXT")
    private String actionableSteps;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RecommendationDecisionStatus status = RecommendationDecisionStatus.PENDING;

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    private String decisionNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by_id")
    private User decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AiRecommendationDecision() {}

    public AiRecommendationDecision(Long id, Supplier supplier, String recommendationRef, String criterionName, String title, RecommendationPriority priority, String recommendationText, String actionableSteps, RecommendationDecisionStatus status, String decisionNotes, User decidedBy, LocalDateTime decidedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplier = supplier;
        this.recommendationRef = recommendationRef;
        this.criterionName = criterionName;
        this.title = title;
        this.priority = priority != null ? priority : RecommendationPriority.MEDIUM;
        this.recommendationText = recommendationText;
        this.actionableSteps = actionableSteps;
        this.status = status != null ? status : RecommendationDecisionStatus.PENDING;
        this.decisionNotes = decisionNotes;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Supplier supplier;
        private String recommendationRef;
        private String criterionName;
        private String title;
        private RecommendationPriority priority = RecommendationPriority.MEDIUM;
        private String recommendationText;
        private String actionableSteps;
        private RecommendationDecisionStatus status = RecommendationDecisionStatus.PENDING;
        private String decisionNotes;
        private User decidedBy;
        private LocalDateTime decidedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder recommendationRef(String recommendationRef) { this.recommendationRef = recommendationRef; return this; }
        public Builder criterionName(String criterionName) { this.criterionName = criterionName; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder priority(RecommendationPriority priority) { this.priority = priority; return this; }
        public Builder recommendationText(String recommendationText) { this.recommendationText = recommendationText; return this; }
        public Builder actionableSteps(String actionableSteps) { this.actionableSteps = actionableSteps; return this; }
        public Builder status(RecommendationDecisionStatus status) { this.status = status; return this; }
        public Builder decisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; return this; }
        public Builder decidedBy(User decidedBy) { this.decidedBy = decidedBy; return this; }
        public Builder decidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public AiRecommendationDecision build() {
            return new AiRecommendationDecision(id, supplier, recommendationRef, criterionName, title, priority, recommendationText, actionableSteps, status, decisionNotes, decidedBy, decidedAt, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getRecommendationRef() { return recommendationRef; }
    public void setRecommendationRef(String recommendationRef) { this.recommendationRef = recommendationRef; }
    public String getCriterionName() { return criterionName; }
    public void setCriterionName(String criterionName) { this.criterionName = criterionName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public RecommendationPriority getPriority() { return priority; }
    public void setPriority(RecommendationPriority priority) { this.priority = priority; }
    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }
    public String getActionableSteps() { return actionableSteps; }
    public void setActionableSteps(String actionableSteps) { this.actionableSteps = actionableSteps; }
    public RecommendationDecisionStatus getStatus() { return status; }
    public void setStatus(RecommendationDecisionStatus status) { this.status = status; }
    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
    public User getDecidedBy() { return decidedBy; }
    public void setDecidedBy(User decidedBy) { this.decidedBy = decidedBy; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
