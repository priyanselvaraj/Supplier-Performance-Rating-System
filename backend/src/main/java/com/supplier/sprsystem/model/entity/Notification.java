package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user_read", columnList = "user_id, is_read"),
        @Index(name = "idx_notif_created_at", columnList = "created_at"),
        @Index(name = "idx_notif_type", columnList = "notification_type")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType = NotificationType.SYSTEM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Column(name = "related_resource_type")
    private String relatedResourceType;

    @Column(name = "related_resource_id")
    private Long relatedResourceId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification() {}

    public Notification(Long id, User user, String title, String message, NotificationType notificationType, NotificationPriority priority, String relatedResourceType, Long relatedResourceId, boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.priority = priority;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private String title;
        private String message;
        private NotificationType notificationType = NotificationType.SYSTEM;
        private NotificationPriority priority = NotificationPriority.MEDIUM;
        private String relatedResourceType;
        private Long relatedResourceId;
        private boolean isRead = false;
        private LocalDateTime createdAt;
        private LocalDateTime readAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder notificationType(NotificationType type) { this.notificationType = type; return this; }
        public Builder priority(NotificationPriority priority) { this.priority = priority; return this; }
        public Builder relatedResourceType(String type) { this.relatedResourceType = type; return this; }
        public Builder relatedResourceId(Long id) { this.relatedResourceId = id; return this; }
        public Builder isRead(boolean isRead) { this.isRead = isRead; return this; }
        public Builder createdAt(LocalDateTime dt) { this.createdAt = dt; return this; }
        public Builder readAt(LocalDateTime dt) { this.readAt = dt; return this; }

        public Notification build() {
            return new Notification(id, user, title, message, notificationType, priority, relatedResourceType, relatedResourceId, isRead, createdAt, readAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }
    public String getRelatedResourceType() { return relatedResourceType; }
    public void setRelatedResourceType(String relatedResourceType) { this.relatedResourceType = relatedResourceType; }
    public Long getRelatedResourceId() { return relatedResourceId; }
    public void setRelatedResourceId(Long relatedResourceId) { this.relatedResourceId = relatedResourceId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
