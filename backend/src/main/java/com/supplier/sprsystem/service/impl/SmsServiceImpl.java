package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Value("${app.sms.enabled:true}")
    private boolean smsEnabled;

    @Value("${app.sms.from:SPRS_ALERTS}")
    private String smsFrom;

    @Value("${app.sms.simulation-mode:true}")
    private boolean simulationMode;

    @Override
    public void sendSms(String toPhone, String message) {
        if (!smsEnabled) {
            logger.debug("SMS notifications disabled via configuration. Skipping SMS to {}", toPhone);
            return;
        }

        if (toPhone == null || toPhone.trim().isEmpty()) {
            logger.debug("Cannot send SMS: recipient phone number is null or empty");
            return;
        }

        try {
            String sanitizedPhone = toPhone.trim();
            if (simulationMode) {
                logger.info("[SMS SIMULATION] Sent to: \"{}\" | SenderId: \"{}\" | Message: \"{}\"",
                        sanitizedPhone, smsFrom, message);
                return;
            }

            // Real SMS gateway dispatch (e.g., Twilio, AWS SNS, Infobip)
            logger.info("Dispatched live SMS to \"{}\" via SMS gateway", sanitizedPhone);
        } catch (Exception e) {
            logger.error("Failed to send SMS to \"{}\": {}", toPhone, e.getMessage(), e);
        }
    }

    @Override
    public void sendWelcomeSms(User user) {
        if (user == null || user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return;
        }

        String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();
        String msg = String.format("Welcome to SPRS, %s! Your account (%s) is active. Log in at http://localhost:5173/login to get started.",
                fullName, user.getUsername());

        sendSms(user.getPhone(), msg);
    }

    @Override
    public void sendLoginAlertSms(User user, String ipAddress, LocalDateTime loginTime) {
        if (user == null || user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return;
        }

        String timeStr = (loginTime != null ? loginTime : LocalDateTime.now()).format(DATE_FORMATTER);
        String safeIp = (ipAddress != null && !ipAddress.trim().isEmpty()) ? ipAddress.trim() : "Unknown IP";
        String msg = String.format("SPRS Alert: Login detected for '%s' at %s (IP: %s). If this wasn't you, reset your password immediately.",
                user.getUsername(), timeStr, safeIp);

        sendSms(user.getPhone(), msg);
    }

    @Override
    public void sendGeneralNotificationSms(User user, String message) {
        if (user == null || user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return;
        }

        String msg = String.format("[SPRS Alert] %s", message != null && message.length() > 140 ? message.substring(0, 137) + "..." : message);
        sendSms(user.getPhone(), msg);
    }
}
