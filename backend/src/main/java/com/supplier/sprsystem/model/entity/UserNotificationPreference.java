package com.supplier.sprsystem.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_notification_preferences", indexes = {
        @Index(name = "idx_pref_user_type", columnList = "user_id, notification_type", unique = true)
})
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private boolean smsEnabled = true;

    public UserNotificationPreference() {}

    public UserNotificationPreference(Long id, User user, NotificationType notificationType, boolean inAppEnabled, boolean emailEnabled, boolean smsEnabled) {
        this.id = id;
        this.user = user;
        this.notificationType = notificationType;
        this.inAppEnabled = inAppEnabled;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private NotificationType notificationType;
        private boolean inAppEnabled = true;
        private boolean emailEnabled = true;
        private boolean smsEnabled = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder notificationType(NotificationType type) { this.notificationType = type; return this; }
        public Builder inAppEnabled(boolean inApp) { this.inAppEnabled = inApp; return this; }
        public Builder emailEnabled(boolean email) { this.emailEnabled = email; return this; }
        public Builder smsEnabled(boolean sms) { this.smsEnabled = sms; return this; }

        public UserNotificationPreference build() {
            return new UserNotificationPreference(id, user, notificationType, inAppEnabled, emailEnabled, smsEnabled);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
}
