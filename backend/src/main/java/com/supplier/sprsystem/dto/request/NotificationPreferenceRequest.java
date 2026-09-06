package com.supplier.sprsystem.dto.request;

import com.supplier.sprsystem.model.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

public class NotificationPreferenceRequest {

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    private boolean inAppEnabled = true;
    private boolean emailEnabled = true;

    public NotificationPreferenceRequest() {}

    public NotificationPreferenceRequest(NotificationType notificationType, boolean inAppEnabled, boolean emailEnabled) {
        this.notificationType = notificationType;
        this.inAppEnabled = inAppEnabled;
        this.emailEnabled = emailEnabled;
    }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
}
