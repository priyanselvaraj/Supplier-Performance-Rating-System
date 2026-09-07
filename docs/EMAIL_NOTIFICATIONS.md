# SPRS Login Email Notification Architecture & Setup Guide

## 1. Overview
The **Supplier Performance Rating System (SPRS)** includes an automated, secure **Login Email Notification** service. Whenever an authenticated user logs into the SPRS web or API application, the system sends an email notification to the user's registered email address.

This security feature provides real-time alerting to users regarding new sign-in sessions, allowing them to detect unauthorized account access immediately.

---

## 2. Authentication & Email Notification Flow

```
[User] (Enters Username/Email + Password)
   ¦
   ?
[AuthController / AuthServiceImpl]
   ¦
   +-? 1. Query user by username/email from UserRepository
   ¦
   +-? 2. Validate BCrypt credentials via AuthenticationManager
   ¦      +-? Authentication FAILS: Return 401 Unauthorized (NO email sent)
   ¦      +-? Authentication SUCCEEDS:
   ¦
   +-? 3. Send Login Notification Email (Target: user.getEmail())
   ¦      +-? Live SMTP Dispatch via JavaMailSender (or Simulation Log)
   ¦      +-? Non-blocking try-catch (failures safely logged, login continues)
   ¦
   +-? 4. Generate JWT Token & Claims
   ¦
   ?
[Return JwtAuthResponse + Redirect to Dashboard]
```

---

## 3. Email Specifications

### Exact Email Subject
```text
SPRS Login Notification
```

### Email Body Content
The email template renders a responsive HTML notification with an RFC-compliant plain text fallback:

- **Greeting**: `Hello, {Full Name / Username}`
- **Login Confirmation**: `Your Supplier Performance Rating System account was successfully logged in.`
- **Login Date**: `{yyyy-MM-dd}` (e.g., `2026-09-07`)
- **Login Time**: `{HH:mm:ss}` (e.g., `14:30:00`)
- **Application Name**: `Supplier Performance Rating System`
- **Session Metadata**: IP Address and Device/Browser client information
- **Security Warning**: `If you did not perform this login, please contact the administrator.`
- **Sign-off**: `Regards,\nSPRS Team`

---

## 4. Environment Configuration & Gmail SMTP Setup

The mail infrastructure is completely externalized and supports standard SMTP providers, specifically **Google Gmail SMTP**.

### Supported Environment Variables

| Variable Name | Alternative Standard | Default | Description |
| :--- | :--- | :--- | :--- |
| `MAIL_HOST` | `SPRING_MAIL_HOST` | `smtp.gmail.com` | SMTP Server Host |
| `MAIL_PORT` | `SPRING_MAIL_PORT` | `587` | SMTP Server Port (587 for TLS, 465 for SSL) |
| `MAIL_USERNAME` | `SPRING_MAIL_USERNAME` | *(empty)* | SMTP Username (e.g., `your-company@gmail.com`) |
| `MAIL_PASSWORD` | `SPRING_MAIL_PASSWORD` | *(empty)* | SMTP Password or Google App Password |
| `MAIL_FROM` | `app.mail.from` | `noreply@sprsystem.com` | Sender address shown in the From header |
| `MAIL_FROM_NAME` | `app.mail.from-name` | `SPRS Notification Center` | Sender display name |
| `MAIL_ENABLED` | `app.mail.enabled` | `true` | Master switch to enable/disable email sending |
| `MAIL_SIMULATION_MODE` | `app.mail.simulation-mode` | `false` | When `true`, logs simulated emails without contacting SMTP |

---

## 5. Gmail App Password Setup Instructions

For Google Gmail accounts with 2-Factor Authentication (2FA) enabled:
1. Navigate to **Google Account Settings** ? **Security**.
2. Under **How you sign in to Google**, select **2-Step Verification**.
3. Scroll to the bottom and select **App passwords**.
4. Enter `SPRS Notification System` as the app name and click **Create**.
5. Copy the generated 16-character password (e.g., `abcd efgh ijkl mnop`).
6. Set the environment variables:
   ```bash
   export MAIL_HOST=smtp.gmail.com
   export MAIL_PORT=587
   export MAIL_USERNAME=your-account@gmail.com
   export MAIL_PASSWORD=abcdefghijklmnop
   export MAIL_FROM=your-account@gmail.com
   ```

---

## 6. Failure Isolation & Resilience (Non-Blocking)

To ensure high availability and unhindered user authentication:
1. **Isolated Execution**: Email dispatch is encapsulated inside safe `try-catch` blocks both within `EmailServiceImpl` and `AuthServiceImpl`.
2. **Zero Login Interruption**: If the SMTP server is unreachable, times out, or encounters authentication errors, the error is logged as a non-blocking warning. The user **still receives their JWT token** and is redirected to the dashboard.
3. **No Secret Leakage**: Passwords, auth tokens, and SMTP credentials are never printed to console logs or exposed in API error payloads.
4. **Health Probe Protection**: Actuator mail health check (`management.health.mail.enabled: false`) is disabled by default to prevent Docker/Kubernetes readiness probes from marking the application container unhealthy when offline or without active SMTP credentials.

---

## 7. Security Rules & Recipient Integrity

- **Strict Recipient Binding**: The recipient address is always bound server-side from `user.getEmail()` of the authenticated entity stored in the database.
- **No Client Injection**: The login endpoint (`/api/v1/auth/login`) accepts only `usernameOrEmail` and `password`. The client has no ability to inject or override the recipient email address.
- **Failed Login Isolation**: When invalid credentials or disabled accounts attempt login, authentication fails immediately (`401 Unauthorized`), and **zero** login-success emails are dispatched.

---

## 8. Verification & Test Suite

The feature is verified by automated test suites:
- **`EmailServiceTest`**: Tests live SMTP MIME formatting, template variable interpolation, null safety, and exception swallowing.
- **`AuthServiceTest`**: Verifies login alert dispatch on successful login, complete absence on failed login, and resilience when email service throws exceptions.
- **`NotificationControllerIntegrationTest`**: Verifies the manual test email endpoint (`POST /api/v1/notifications/test-email`).
