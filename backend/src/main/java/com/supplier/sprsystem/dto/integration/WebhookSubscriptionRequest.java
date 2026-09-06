package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.WebhookEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class WebhookSubscriptionRequest {

    @NotBlank(message = "Webhook name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Target URL is required")
    @Pattern(regexp = "^(https?://.+)$", message = "Target URL must start with http:// or https://")
    @Size(max = 500, message = "Target URL must not exceed 500 characters")
    private String targetUrl;

    @NotEmpty(message = "At least one event type is required")
    private Set<WebhookEventType> eventTypes;

    @Size(max = 128)
    private String secretToken; // Optional: if provided by client, otherwise auto-generated

    public WebhookSubscriptionRequest() {
    }

    public WebhookSubscriptionRequest(String name, String targetUrl, Set<WebhookEventType> eventTypes, String secretToken) {
        this.name = name;
        this.targetUrl = targetUrl;
        this.eventTypes = eventTypes;
        this.secretToken = secretToken;
    }

    // Getters and Setters
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

    public Set<WebhookEventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Set<WebhookEventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }
}
