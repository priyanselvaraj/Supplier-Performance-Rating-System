package com.supplier.sprsystem.dto.integration;

import com.supplier.sprsystem.model.entity.ApiKeyScope;

import java.time.LocalDateTime;
import java.util.Set;

public class ApiKeyCreatedResponse extends ApiKeyResponse {

    private String rawApiKey;
    private String warning;

    public ApiKeyCreatedResponse() {
    }

    public ApiKeyCreatedResponse(Long id, String name, String keyPrefix, Set<ApiKeyScope> scopes,
                                 boolean active, LocalDateTime expiresAt, LocalDateTime lastUsedAt,
                                 int rateLimitPerMinute, String createdByName,
                                 LocalDateTime createdAt, LocalDateTime updatedAt,
                                 String rawApiKey) {
        super(id, name, keyPrefix, scopes, active, expiresAt, lastUsedAt, rateLimitPerMinute, createdByName, createdAt, updatedAt);
        this.rawApiKey = rawApiKey;
        this.warning = "Please copy your API key now. It will NEVER be shown again in full!";
    }

    public String getRawApiKey() {
        return rawApiKey;
    }

    public void setRawApiKey(String rawApiKey) {
        this.rawApiKey = rawApiKey;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
