package com.supplier.sprsystem.service;

import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.User;

import java.time.LocalDateTime;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String bodyHtml, String plainTextFallback);

    void sendWelcomeEmail(User user);

    void sendLoginAlertEmail(User user, String ipAddress, String userAgent, LocalDateTime loginTime);

    void sendGeneralNotificationEmail(User user, String title, String message, NotificationType type);
}
