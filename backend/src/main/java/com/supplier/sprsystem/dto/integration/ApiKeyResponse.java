package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.ApiKeyScope;

import java.time.LocalDateTime;
import java.util.Set;

public class ApiKeyResponse {

    private Long id;
    private String name;
    private String keyPrefix;
    private Set<ApiKeyScope> scopes;
    private boolean active;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private int rateLimitPerMinute;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApiKeyResponse() {
    }

    public ApiKeyResponse(Long id, String name, String keyPrefix, Set<ApiKeyScope> scopes,
                          boolean active, LocalDateTime expiresAt, LocalDateTime lastUsedAt,
                          int rateLimitPerMinute, String createdByName,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.keyPrefix = keyPrefix;
        this.scopes = scopes;
        this.active = active;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.createdByName = createdByName;
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

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public Set<ApiKeyScope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ApiKeyScope> scopes) {
        this.scopes = scopes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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
