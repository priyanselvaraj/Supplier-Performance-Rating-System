package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

public class NotificationPreferenceRequest {

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    private boolean inAppEnabled = true;
    private boolean emailEnabled = true;
    private boolean smsEnabled = true;

    public NotificationPreferenceRequest() {}

    public NotificationPreferenceRequest(NotificationType notificationType, boolean inAppEnabled, boolean emailEnabled, boolean smsEnabled) {
        this.notificationType = notificationType;
        this.inAppEnabled = inAppEnabled;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
    }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
}
