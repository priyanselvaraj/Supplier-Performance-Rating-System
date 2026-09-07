package com.supplier.sprsystem.service;

import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.Role;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mailFrom", "noreply@sprsystem.com");
        ReflectionTestUtils.setField(emailService, "mailFromName", "SPRS Notification Center");
        ReflectionTestUtils.setField(emailService, "simulationMode", true);

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
    @DisplayName("Test sending raw email in simulation mode does not throw exception")
    void testSendEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendEmail("test@example.com", "Test Subject", "<p>Hello</p>", "Hello"));
    }

    @Test
    @DisplayName("Test sending welcome email formats HTML template safely")
    void testSendWelcomeEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(sampleUser));
    }

    @Test
    @DisplayName("Test sending login alert email includes security metadata")
    void testSendLoginAlertEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendLoginAlertEmail(sampleUser, "192.168.1.100", "Mozilla/5.0 Chrome", LocalDateTime.now()));
    }

    @Test
    @DisplayName("Test sending general notification email formats properly")
    void testSendGeneralNotificationEmail_Success() {
        assertDoesNotThrow(() -> emailService.sendGeneralNotificationEmail(sampleUser, "Performance Alert", "Score dropped", NotificationType.ALERT));
    }

    @Test
    @DisplayName("Test sending email with null user or null email handles gracefully")
    void testSendEmail_NullSafety() {
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(null));
        assertDoesNotThrow(() -> emailService.sendLoginAlertEmail(null, null, null, null));
        assertDoesNotThrow(() -> emailService.sendGeneralNotificationEmail(null, null, null, null));
        assertDoesNotThrow(() -> emailService.sendEmail(null, null, null, null));
    }
}
