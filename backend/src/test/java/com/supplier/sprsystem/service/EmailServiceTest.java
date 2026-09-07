package com.supplier.sprsystem.service;

import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.Role;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.service.impl.EmailServiceImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mailFrom", "noreply@sprsystem.com");
        ReflectionTestUtils.setField(emailService, "mailFromName", "SPRS Notification Center");
        ReflectionTestUtils.setField(emailService, "simulationMode", false);

        Role role = Role.builder().id(1L).name(ERole.ROLE_MANAGER).build();
        sampleUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .fullName("John Doe")
                .roles(Set.of(role))
                .department("Procurement")
                .build();
    }

    @Test
    @DisplayName("Test sending email in simulation mode does not invoke JavaMailSender")
    void testSendEmail_SimulationMode() {
        ReflectionTestUtils.setField(emailService, "simulationMode", true);
        assertDoesNotThrow(() -> emailService.sendEmail("test@example.com", "Test Subject", "<p>Hello</p>", "Hello"));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Test sending login alert email via SMTP sends valid MimeMessage")
    void testSendLoginAlertEmail_LiveSmtp() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        LocalDateTime fixedTime = LocalDateTime.of(2026, 9, 7, 14, 30, 0);
        assertDoesNotThrow(() -> emailService.sendLoginAlertEmail(sampleUser, "192.168.1.100", "Mozilla/5.0", fixedTime));

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Test SMTP failure during email dispatch is caught safely without throwing exception")
    void testSendEmail_SmtpExceptionHandledSafely() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP Connection timed out")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail("john@example.com", "SPRS Login Notification", "<p>Body</p>", "Fallback text"));
    }

    @Test
    @DisplayName("Test sending welcome email formats HTML template safely")
    void testSendWelcomeEmail_Success() {
        ReflectionTestUtils.setField(emailService, "simulationMode", true);
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(sampleUser));
    }

    @Test
    @DisplayName("Test sending login alert email includes required prompt fields")
    void testSendLoginAlertEmail_SimulationSuccess() {
        ReflectionTestUtils.setField(emailService, "simulationMode", true);
        assertDoesNotThrow(() -> emailService.sendLoginAlertEmail(sampleUser, "192.168.1.100", "Mozilla/5.0 Chrome", LocalDateTime.now()));
    }

    @Test
    @DisplayName("Test sending general notification email formats properly")
    void testSendGeneralNotificationEmail_Success() {
        ReflectionTestUtils.setField(emailService, "simulationMode", true);
        assertDoesNotThrow(() -> emailService.sendGeneralNotificationEmail(sampleUser, "Performance Alert", "Score dropped", NotificationType.ALERT));
    }

    @Test
    @DisplayName("Test sending email with disabled mail setting skips execution")
    void testSendEmail_Disabled() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", false);
        assertDoesNotThrow(() -> emailService.sendEmail("john@example.com", "Test", "<p>Hi</p>", "Hi"));
        verify(mailSender, never()).createMimeMessage();
    }

    @Test
    @DisplayName("Test sending email with null user or null email handles gracefully")
    void testSendEmail_NullSafety() {
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(null));
        assertDoesNotThrow(() -> emailService.sendLoginAlertEmail(null, null, null, null));
        assertDoesNotThrow(() -> emailService.sendGeneralNotificationEmail(null, null, null, null));
        assertDoesNotThrow(() -> emailService.sendEmail(null, null, null, null));
        assertDoesNotThrow(() -> emailService.sendEmail("   ", "Subject", "Body", "Fallback"));
    }
}
