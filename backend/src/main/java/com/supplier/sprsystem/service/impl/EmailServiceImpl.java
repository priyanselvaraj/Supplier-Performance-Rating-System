package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@sprsystem.com}")
    private String mailFrom;

    @Value("${app.mail.from-name:SPRS Notification Center}")
    private String mailFromName;

    @Value("${app.mail.simulation-mode:false}")
    private boolean simulationMode;

    public EmailServiceImpl(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String toEmail, String subject, String bodyHtml, String plainTextFallback) {
        if (!mailEnabled) {
            logger.debug("Email notifications disabled via configuration. Skipping email to {}", toEmail);
            return;
        }

        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("Cannot send email: recipient address is null or empty");
            return;
        }

        try {
            if (simulationMode || mailSender == null) {
                logger.info("[EMAIL SIMULATION] Sent to: <{}> | Subject: \"{}\" | From: \"{}\" <{}> | Body Length: {} chars",
                        toEmail, subject, mailFromName, mailFrom, bodyHtml != null ? bodyHtml.length() : 0);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(mailFrom, mailFromName));
            helper.setTo(toEmail.trim());
            helper.setSubject(subject);

            if (bodyHtml != null && !bodyHtml.trim().isEmpty()) {
                helper.setText(plainTextFallback != null ? plainTextFallback : bodyHtml, bodyHtml);
            } else {
                helper.setText(plainTextFallback != null ? plainTextFallback : subject, false);
            }

            mailSender.send(message);
            logger.info("Successfully dispatched live email via SMTP to <{}> with subject \"{}\"", toEmail, subject);
        } catch (Exception e) {
            logger.error("Failed to send email to <{}> via SMTP: {}. Falling back to simulated log.", toEmail, e.getMessage());
            logger.info("[EMAIL FALLBACK LOG] To: <{}> | Subject: \"{}\" | Body: {}", toEmail, subject, plainTextFallback != null ? plainTextFallback : subject);
        }
    }

    @Override
    public void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "🎉 Welcome to SPRS - Supplier Performance Rating System";
        String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();
        String roleText = user.getRoles() != null && !user.getRoles().isEmpty()
                ? user.getRoles().iterator().next().getName().name().replace("ROLE_", "")
                : "USER";

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #1e293b; margin: 0; padding: 24px; }
                        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
                        .header { background: linear-gradient(135deg, #1e40af, #3b82f6); color: #ffffff; padding: 32px 24px; text-align: center; }
                        .header h1 { margin: 0; font-size: 22px; font-weight: 700; letter-spacing: -0.025em; }
                        .content { padding: 32px 24px; line-height: 1.6; }
                        .badge { display: inline-block; background-color: #dbeafe; color: #1e40af; font-weight: 600; font-size: 12px; padding: 4px 10px; border-radius: 9999px; }
                        .card { background-color: #f1f5f9; border-radius: 8px; padding: 16px 20px; margin: 20px 0; }
                        .card-item { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
                        .card-item:last-child { margin-bottom: 0; }
                        .btn { display: inline-block; background-color: #2563eb; color: #ffffff; text-decoration: none; padding: 12px 24px; font-weight: 600; border-radius: 8px; margin-top: 16px; }
                        .footer { padding: 20px 24px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #f1f5f9; background: #fafafa; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Supplier Performance Rating System</h1>
                        </div>
                        <div class="content">
                            <h2>Welcome, %s!</h2>
                            <p>Your account on <strong>SPRS</strong> has been successfully registered and activated.</p>
                            
                            <div class="card">
                                <div class="card-item"><strong>Username:</strong> %s</div>
                                <div class="card-item"><strong>Email:</strong> %s</div>
                                <div class="card-item"><strong>Assigned Role:</strong> <span class="badge">%s</span></div>
                                <div class="card-item"><strong>Department:</strong> %s</div>
                            </div>

                            <p>You can now log in to the portal to manage supplier evaluations, track real-time ratings, and access AI-driven performance insights.</p>
                            
                            <p style="text-align: center;">
                                <a href="http://localhost:5173/login" class="btn" style="color: #ffffff;">Access Your Dashboard</a>
                            </p>
                            
                            <p style="font-size: 13px; color: #64748b; margin-top: 24px;">
                                <em>Security Notice: If you did not create this account, please contact your system administrator immediately.</em>
                            </p>
                        </div>
                        <div class="footer">
                            &copy; %d Supplier Performance Rating System. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                fullName,
                user.getUsername(),
                user.getEmail(),
                roleText,
                user.getDepartment() != null ? user.getDepartment() : "General",
                LocalDateTime.now().getYear()
        );

        String plainText = "Welcome to SPRS, " + fullName + "!\n\n"
                + "Your account (" + user.getUsername() + ") has been successfully registered.\n"
                + "Role: " + roleText + "\n"
                + "Email: " + user.getEmail() + "\n\n"
                + "Log in at http://localhost:5173/login to access your dashboard.";

        sendEmail(user.getEmail(), subject, html, plainText);
    }

    @Override
    public void sendLoginAlertEmail(User user, String ipAddress, String userAgent, LocalDateTime loginTime) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) return;

        LocalDateTime timestamp = (loginTime != null) ? loginTime : LocalDateTime.now();
        String loginDate = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String loginTimeStr = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String subject = "SPRS Login Notification";
        String fullName = (user.getFullName() != null && !user.getFullName().trim().isEmpty()) ? user.getFullName() : user.getUsername();
        String safeIp = (ipAddress != null && !ipAddress.trim().isEmpty()) ? ipAddress.trim() : "Unknown IP";
        String safeAgent = (userAgent != null && !userAgent.trim().isEmpty()) ? userAgent.trim() : "Web Browser / API Client";

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #1e293b; margin: 0; padding: 24px; }
                        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
                        .header { background: linear-gradient(135deg, #0f172a, #1e293b); color: #ffffff; padding: 28px 24px; text-align: center; }
                        .header h1 { margin: 0; font-size: 20px; font-weight: 700; }
                        .content { padding: 32px 24px; line-height: 1.6; }
                        .alert-box { background-color: #eff6ff; border-left: 4px solid #3b82f6; border-radius: 4px; padding: 16px; margin: 20px 0; }
                        .card-item { margin-bottom: 8px; font-size: 14px; }
                        .card-item:last-child { margin-bottom: 0; }
                        .warning-box { background-color: #fef2f2; border: 1px solid #fee2e2; border-radius: 6px; padding: 12px 16px; color: #991b1b; font-size: 13px; margin-top: 20px; }
                        .footer { padding: 20px 24px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #f1f5f9; background: #fafafa; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Supplier Performance Rating System</h1>
                        </div>
                        <div class="content">
                            <h2>Hello, %s</h2>
                            <p>Your <strong>Supplier Performance Rating System</strong> account was successfully logged in.</p>
                            
                            <div class="alert-box">
                                <div class="card-item"><strong>Login Date:</strong> %s</div>
                                <div class="card-item"><strong>Login Time:</strong> %s</div>
                                <div class="card-item"><strong>Application:</strong> Supplier Performance Rating System</div>
                                <div class="card-item"><strong>Account:</strong> %s (%s)</div>
                                <div class="card-item"><strong>IP Address:</strong> %s</div>
                                <div class="card-item"><strong>Device / Browser:</strong> %s</div>
                            </div>

                            <div class="warning-box">
                                <strong>Security Warning:</strong> If you did not perform this login, please contact the administrator.
                            </div>

                            <p style="margin-top: 24px; line-height: 1.5;">
                                Regards,<br>
                                <strong>SPRS Team</strong>
                            </p>
                        </div>
                        <div class="footer">
                            &copy; %d Supplier Performance Rating System &bull; All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                fullName,
                loginDate,
                loginTimeStr,
                user.getUsername(),
                user.getEmail(),
                safeIp,
                safeAgent,
                LocalDateTime.now().getYear()
        );

        String plainText = "Hello,\n\n"
                + "Your Supplier Performance Rating System account was successfully logged in.\n\n"
                + "Login Date: " + loginDate + "\n"
                + "Login Time: " + loginTimeStr + "\n"
                + "Application: Supplier Performance Rating System\n\n"
                + "If you did not perform this login, please contact the administrator.\n\n"
                + "Regards,\nSPRS Team";

        sendEmail(user.getEmail(), subject, html, plainText);
    }

    @Override
    public void sendGeneralNotificationEmail(User user, String title, String message, NotificationType type) {
        if (user == null || user.getEmail() == null) return;

        String typeName = type != null ? type.getDisplayName() : "Notification";
        String subject = "[SPRS " + typeName + "] " + title;
        String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #1e293b; margin: 0; padding: 24px; }
                        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; }
                        .header { background: #1e40af; color: #ffffff; padding: 20px 24px; font-weight: 700; font-size: 18px; }
                        .content { padding: 28px 24px; }
                        .msg-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px; margin: 16px 0; font-size: 14px; line-height: 1.6; }
                        .footer { padding: 16px 24px; text-align: center; font-size: 12px; color: #64748b; background: #fafafa; border-top: 1px solid #f1f5f9; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">SPRS Notification: %s</div>
                        <div class="content">
                            <p>Hello %s,</p>
                            <h3 style="margin-top: 0;">%s</h3>
                            <div class="msg-box">%s</div>
                            <p style="font-size: 13px; color: #64748b;">You can view and manage all your notifications inside the SPRS portal.</p>
                        </div>
                        <div class="footer">&copy; %d Supplier Performance Rating System</div>
                    </div>
                </body>
                </html>
                """.formatted(typeName, fullName, title, message, LocalDateTime.now().getYear());

        sendEmail(user.getEmail(), subject, html, message);
    }
}
