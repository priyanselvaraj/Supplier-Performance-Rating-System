package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.WebhookEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class WebhookPayload<T> {

    private String eventId;
    private WebhookEventType eventType;
    private LocalDateTime timestamp;
    private T data;

    public WebhookPayload() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public WebhookPayload(WebhookEventType eventType, T data) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public WebhookEventType getEventType() {
        return eventType;
    }

    public void setEventType(WebhookEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
