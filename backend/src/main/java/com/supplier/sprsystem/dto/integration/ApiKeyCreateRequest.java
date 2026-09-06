package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.ApiKeyScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public class ApiKeyCreateRequest {

    @NotBlank(message = "API Key name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotEmpty(message = "At least one scope is required")
    private Set<ApiKeyScope> scopes;

    private LocalDateTime expiresAt;

    private int rateLimitPerMinute = 60;

    public ApiKeyCreateRequest() {
    }

    public ApiKeyCreateRequest(String name, Set<ApiKeyScope> scopes, LocalDateTime expiresAt, int rateLimitPerMinute) {
        this.name = name;
        this.scopes = scopes;
        this.expiresAt = expiresAt;
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ApiKeyScope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ApiKeyScope> scopes) {
        this.scopes = scopes;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }
}
