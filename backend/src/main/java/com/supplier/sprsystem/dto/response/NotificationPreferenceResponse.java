package com.supplier.sprsystem.dto.response;

import com.supplier.sprsystem.model.entity.NotificationType;

public class NotificationPreferenceResponse {
    private Long id;
    private NotificationType notificationType;
    private String displayName;
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean smsEnabled;

    public NotificationPreferenceResponse() {}

    public NotificationPreferenceResponse(Long id, NotificationType notificationType, String displayName, boolean inAppEnabled, boolean emailEnabled, boolean smsEnabled) {
        this.id = id;
        this.notificationType = notificationType;
        this.displayName = displayName;
        this.inAppEnabled = inAppEnabled;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private NotificationType notificationType;
        private String displayName;
        private boolean inAppEnabled;
        private boolean emailEnabled;
        private boolean smsEnabled;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder notificationType(NotificationType type) { this.notificationType = type; return this; }
        public Builder displayName(String name) { this.displayName = name; return this; }
        public Builder inAppEnabled(boolean inApp) { this.inAppEnabled = inApp; return this; }
        public Builder emailEnabled(boolean email) { this.emailEnabled = email; return this; }
        public Builder smsEnabled(boolean sms) { this.smsEnabled = sms; return this; }

        public NotificationPreferenceResponse build() {
            return new NotificationPreferenceResponse(id, notificationType, displayName, inAppEnabled, emailEnabled, smsEnabled);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
}
