package com.supplier.sprsystem.service;

import com.supplier.sprsystem.model.entity.User;

import java.time.LocalDateTime;

public interface SmsService {

    void sendSms(String toPhone, String message);

    void sendWelcomeSms(User user);

    void sendLoginAlertSms(User user, String ipAddress, LocalDateTime loginTime);

    void sendGeneralNotificationSms(User user, String message);
}
