package com.supplier.sprsystem.service;

import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.service.impl.SmsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTest {

    @InjectMocks
    private SmsServiceImpl smsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smsService, "smsEnabled", true);
        ReflectionTestUtils.setField(smsService, "smsFrom", "SPRS_ALERTS");
        ReflectionTestUtils.setField(smsService, "simulationMode", true);

        sampleUser = User.builder()
                .id(1L)
                .username("john_doe")
                .fullName("John Doe")
                .phone("+1 555-0199")
                .build();
    }

    @Test
    @DisplayName("Test sending SMS does not throw exception")
    void testSendSms_Success() {
        assertDoesNotThrow(() -> smsService.sendSms("+1 555-0199", "Test alert"));
    }

    @Test
    @DisplayName("Test welcome SMS dispatch")
    void testSendWelcomeSms_Success() {
        assertDoesNotThrow(() -> smsService.sendWelcomeSms(sampleUser));
    }

    @Test
    @DisplayName("Test login alert SMS dispatch")
    void testSendLoginAlertSms_Success() {
        assertDoesNotThrow(() -> smsService.sendLoginAlertSms(sampleUser, "192.168.1.100", LocalDateTime.now()));
    }

    @Test
    @DisplayName("Test general notification SMS dispatch")
    void testSendGeneralNotificationSms_Success() {
        assertDoesNotThrow(() -> smsService.sendGeneralNotificationSms(sampleUser, "Important update"));
    }

    @Test
    @DisplayName("Test null safety on SMS operations")
    void testSendSms_NullSafety() {
        assertDoesNotThrow(() -> smsService.sendWelcomeSms(null));
        assertDoesNotThrow(() -> smsService.sendLoginAlertSms(null, null, null));
        assertDoesNotThrow(() -> smsService.sendGeneralNotificationSms(null, null));
        assertDoesNotThrow(() -> smsService.sendSms(null, null));
    }
}
