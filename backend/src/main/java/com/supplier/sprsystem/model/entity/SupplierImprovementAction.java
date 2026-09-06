package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_improvement_actions", indexes = {
        @Index(name = "idx_action_supplier", columnList = "supplier_id"),
        @Index(name = "idx_action_status", columnList = "status"),
        @Index(name = "idx_action_assigned", columnList = "assigned_user_id"),
        @Index(name = "idx_action_due_date", columnList = "due_date")
})
public class SupplierImprovementAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImprovementActionPriority priority = ImprovementActionPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImprovementActionStatus status = ImprovementActionStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SupplierImprovementAction() {}

    public SupplierImprovementAction(Long id, Supplier supplier, String title, String description, ImprovementActionPriority priority, ImprovementActionStatus status, User assignedUser, User createdByUser, LocalDate dueDate, LocalDateTime completedAt, String resolutionNotes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplier = supplier;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedUser = assignedUser;
        this.createdByUser = createdByUser;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Supplier supplier;
        private String title;
        private String description;
        private ImprovementActionPriority priority = ImprovementActionPriority.MEDIUM;
        private ImprovementActionStatus status = ImprovementActionStatus.OPEN;
        private User assignedUser;
        private User createdByUser;
        private LocalDate dueDate;
        private LocalDateTime completedAt;
        private String resolutionNotes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder priority(ImprovementActionPriority priority) { this.priority = priority; return this; }
        public Builder status(ImprovementActionStatus status) { this.status = status; return this; }
        public Builder assignedUser(User user) { this.assignedUser = user; return this; }
        public Builder createdByUser(User user) { this.createdByUser = user; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SupplierImprovementAction build() {
            return new SupplierImprovementAction(id, supplier, title, description, priority, status, assignedUser, createdByUser, dueDate, completedAt, resolutionNotes, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ImprovementActionPriority getPriority() { return priority; }
    public void setPriority(ImprovementActionPriority priority) { this.priority = priority; }
    public ImprovementActionStatus getStatus() { return status; }
    public void setStatus(ImprovementActionStatus status) { this.status = status; }
    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) { this.assignedUser = assignedUser; }
    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
