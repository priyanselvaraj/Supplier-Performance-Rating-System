package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.WebhookDeliveryLogResponse;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionRequest;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionResponse;
import com.supplier.sprsystem.model.entity.WebhookDeliveryLog;
import com.supplier.sprsystem.model.entity.WebhookDeliveryStatus;
import com.supplier.sprsystem.model.entity.WebhookEventType;
import com.supplier.sprsystem.model.entity.WebhookSubscription;
import com.supplier.sprsystem.repository.WebhookDeliveryLogRepository;
import com.supplier.sprsystem.repository.WebhookSubscriptionRepository;
import com.supplier.sprsystem.service.impl.WebhookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebhookServiceTest {

    @Mock
    private WebhookSubscriptionRepository subscriptionRepository;

    @Mock
    private WebhookDeliveryLogRepository deliveryLogRepository;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private WebhookServiceImpl webhookService;

    private WebhookSubscription sampleSubscription;

    @BeforeEach
    void setUp() {
        sampleSubscription = new WebhookSubscription(
                "ERP Webhook",
                "https://example.com/webhook",
                "whsec_testsecret123456789",
                Set.of(WebhookEventType.SUPPLIER_CREATED, WebhookEventType.SUPPLIER_UPDATED)
        );
        sampleSubscription.setId(1L);
    }

    @Test
    @DisplayName("Create Webhook subscription generates secret token if not provided")
    void testCreateSubscription() {
        when(subscriptionRepository.save(any(WebhookSubscription.class))).thenAnswer(invocation -> {
            WebhookSubscription sub = invocation.getArgument(0);
            sub.setId(1L);
            return sub;
        });

        WebhookSubscriptionRequest request = new WebhookSubscriptionRequest(
                "ERP Webhook",
                "https://example.com/webhook",
                Set.of(WebhookEventType.SUPPLIER_CREATED),
                null
        );

        WebhookSubscriptionResponse response = webhookService.createSubscription(request);

        assertNotNull(response);
        assertEquals("ERP Webhook", response.getName());
        assertEquals("https://example.com/webhook", response.getTargetUrl());
        assertNotNull(response.getSecretToken());
        assertTrue(response.getSecretToken().startsWith("whsec_"));
        verify(subscriptionRepository, times(1)).save(any(WebhookSubscription.class));
    }

    @Test
    @DisplayName("Toggle Webhook subscription status")
    void testToggleStatus() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(sampleSubscription));
        when(subscriptionRepository.save(any(WebhookSubscription.class))).thenReturn(sampleSubscription);

        WebhookSubscriptionResponse response = webhookService.toggleSubscriptionStatus(1L, false);

        assertNotNull(response);
        assertFalse(response.isActive());
        verify(subscriptionRepository, times(1)).save(sampleSubscription);
    }

    @Test
    @DisplayName("HMAC-SHA256 signature calculation produces consistent hex string")
    void testHmacSha256() {
        String payload = "{\"test\":true,\"value\":123}";
        String secret = "my_secret_token_123";

        String sig1 = WebhookServiceImpl.calculateHmacSha256(payload, secret);
        String sig2 = WebhookServiceImpl.calculateHmacSha256(payload, secret);

        assertNotNull(sig1);
        assertEquals(64, sig1.length());
        assertEquals(sig1, sig2);
    }

    @Test
    @DisplayName("Get recent delivery logs returns mapped DTOs")
    void testGetRecentDeliveryLogs() {
        WebhookDeliveryLog log = new WebhookDeliveryLog(
                sampleSubscription,
                WebhookEventType.TEST_PING,
                "https://example.com/webhook",
                "{\"message\":\"ping\"}"
        );
        log.setId(10L);
        log.setStatus(WebhookDeliveryStatus.SUCCESS);
        log.setResponseStatus(200);

        when(deliveryLogRepository.findTop100ByOrderByDeliveredAtDesc()).thenReturn(List.of(log));

        List<WebhookDeliveryLogResponse> logs = webhookService.getRecentDeliveryLogs();

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(200, logs.get(0).getResponseStatus());
        assertEquals(WebhookDeliveryStatus.SUCCESS, logs.get(0).getStatus());
    }
}
