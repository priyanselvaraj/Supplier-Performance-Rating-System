package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.WebhookDeliveryLogResponse;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionRequest;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionResponse;
import com.supplier.sprsystem.model.entity.WebhookEventType;

import java.util.List;

public interface WebhookService {

    WebhookSubscriptionResponse createSubscription(WebhookSubscriptionRequest request);

    List<WebhookSubscriptionResponse> getAllSubscriptions();

    WebhookSubscriptionResponse getSubscriptionById(Long id);

    WebhookSubscriptionResponse updateSubscription(Long id, WebhookSubscriptionRequest request);

    WebhookSubscriptionResponse toggleSubscriptionStatus(Long id, boolean active);

    void deleteSubscription(Long id);

    void dispatchEvent(WebhookEventType eventType, Object eventData);

    WebhookDeliveryLogResponse sendTestPing(Long subscriptionId);

    List<WebhookDeliveryLogResponse> getRecentDeliveryLogs();

    List<WebhookDeliveryLogResponse> getLogsBySubscription(Long subscriptionId);
}
