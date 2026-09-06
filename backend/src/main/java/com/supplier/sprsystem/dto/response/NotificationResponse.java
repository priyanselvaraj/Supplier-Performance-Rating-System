package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.NotificationPriority;
import com.supplier.sprsystem.model.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String message;
    private NotificationType notificationType;
    private NotificationPriority priority;
    private String relatedResourceType;
    private Long relatedResourceId;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, Long userId, String username, String title, String message, NotificationType notificationType, NotificationPriority priority, String relatedResourceType, Long relatedResourceId, boolean read, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.priority = priority;
        this.relatedResourceType = relatedResourceType;
        this.relatedResourceId = relatedResourceId;
        this.read = read;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String username;
        private String title;
        private String message;
        private NotificationType notificationType;
        private NotificationPriority priority;
        private String relatedResourceType;
        private Long relatedResourceId;
        private boolean read;
        private LocalDateTime createdAt;
        private LocalDateTime readAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder notificationType(NotificationType type) { this.notificationType = type; return this; }
        public Builder priority(NotificationPriority priority) { this.priority = priority; return this; }
        public Builder relatedResourceType(String type) { this.relatedResourceType = type; return this; }
        public Builder relatedResourceId(Long id) { this.relatedResourceId = id; return this; }
        public Builder read(boolean read) { this.read = read; return this; }
        public Builder createdAt(LocalDateTime dt) { this.createdAt = dt; return this; }
        public Builder readAt(LocalDateTime dt) { this.readAt = dt; return this; }

        public NotificationResponse build() {
            return new NotificationResponse(id, userId, username, title, message, notificationType, priority, relatedResourceType, relatedResourceId, read, createdAt, readAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
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
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
