package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.WebhookEventType;

import java.time.LocalDateTime;
import java.util.Set;

public class WebhookSubscriptionResponse {

    private Long id;
    private String name;
    private String targetUrl;
    private String secretToken; // masked or full if admin
    private Set<WebhookEventType> eventTypes;
    private boolean active;
    private int failureCount;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastFailureAt;
    private String lastErrorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WebhookSubscriptionResponse() {
    }

    public WebhookSubscriptionResponse(Long id, String name, String targetUrl, String secretToken,
                                       Set<WebhookEventType> eventTypes, boolean active, int failureCount,
                                       LocalDateTime lastSuccessAt, LocalDateTime lastFailureAt,
                                       String lastErrorMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.targetUrl = targetUrl;
        this.secretToken = secretToken;
        this.eventTypes = eventTypes;
        this.active = active;
        this.failureCount = failureCount;
        this.lastSuccessAt = lastSuccessAt;
        this.lastFailureAt = lastFailureAt;
        this.lastErrorMessage = lastErrorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public Set<WebhookEventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Set<WebhookEventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public LocalDateTime getLastSuccessAt() {
        return lastSuccessAt;
    }

    public void setLastSuccessAt(LocalDateTime lastSuccessAt) {
        this.lastSuccessAt = lastSuccessAt;
    }

    public LocalDateTime getLastFailureAt() {
        return lastFailureAt;
    }

    public void setLastFailureAt(LocalDateTime lastFailureAt) {
        this.lastFailureAt = lastFailureAt;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
