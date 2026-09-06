package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.ApiKeyCreateRequest;
import com.supplier.sprsystem.dto.integration.ApiKeyCreatedResponse;
import com.supplier.sprsystem.dto.integration.ApiKeyResponse;
import com.supplier.sprsystem.model.entity.ApiKey;
import com.supplier.sprsystem.model.entity.ApiKeyScope;

import java.util.List;

public interface ApiKeyService {

    ApiKeyCreatedResponse generateApiKey(ApiKeyCreateRequest request, String createdBy);

    List<ApiKeyResponse> getAllApiKeys();

    ApiKeyResponse getApiKeyById(Long id);

    ApiKeyResponse toggleApiKeyStatus(Long id, boolean active);

    void deleteApiKey(Long id);

    boolean validateScope(ApiKey apiKey, ApiKeyScope requiredScope);
}
