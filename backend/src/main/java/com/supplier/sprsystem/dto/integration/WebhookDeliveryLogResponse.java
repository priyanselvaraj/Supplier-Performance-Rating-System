package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.WebhookDeliveryStatus;
import com.supplier.sprsystem.model.entity.WebhookEventType;

import java.time.LocalDateTime;

public class WebhookDeliveryLogResponse {

    private Long id;
    private Long subscriptionId;
    private String subscriptionName;
    private WebhookEventType eventType;
    private String targetUrl;
    private String payload;
    private Integer responseStatus;
    private String responseBody;
    private Long durationMs;
    private WebhookDeliveryStatus status;
    private int attemptCount;
    private String errorMessage;
    private LocalDateTime deliveredAt;

    public WebhookDeliveryLogResponse() {
    }

    public WebhookDeliveryLogResponse(Long id, Long subscriptionId, String subscriptionName,
                                      WebhookEventType eventType, String targetUrl, String payload,
                                      Integer responseStatus, String responseBody, Long durationMs,
                                      WebhookDeliveryStatus status, int attemptCount,
                                      String errorMessage, LocalDateTime deliveredAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.subscriptionName = subscriptionName;
        this.eventType = eventType;
        this.targetUrl = targetUrl;
        this.payload = payload;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.durationMs = durationMs;
        this.status = status;
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.deliveredAt = deliveredAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public WebhookEventType getEventType() {
        return eventType;
    }

    public void setEventType(WebhookEventType eventType) {
        this.eventType = eventType;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public WebhookDeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookDeliveryStatus status) {
        this.status = status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}
