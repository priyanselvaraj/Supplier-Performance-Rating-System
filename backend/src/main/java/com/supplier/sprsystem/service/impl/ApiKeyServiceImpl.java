package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.integration.ApiKeyCreateRequest;
import com.supplier.sprsystem.dto.integration.ApiKeyCreatedResponse;
import com.supplier.sprsystem.dto.integration.ApiKeyResponse;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.ApiKey;
import com.supplier.sprsystem.model.entity.ApiKeyScope;
import com.supplier.sprsystem.repository.ApiKeyRepository;
import com.supplier.sprsystem.security.ApiKeyAuthenticationFilter;
import com.supplier.sprsystem.service.ApiKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public ApiKeyCreatedResponse generateApiKey(ApiKeyCreateRequest request, String createdBy) {
        if (apiKeyRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("An API key with name '" + request.getName() + "' already exists");
        }

        // Generate 16 secure random bytes (32 hex characters)
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        String randomHex = HexFormat.of().formatHex(randomBytes);

        String rawKey = "sprs_live_" + randomHex;
        String keyPrefix = rawKey.substring(0, 14); // e.g. sprs_live_a1b2
        String keyHash = ApiKeyAuthenticationFilter.hashKey(rawKey);

        ApiKey apiKey = new ApiKey(
                request.getName(),
                keyPrefix,
                keyHash,
                request.getScopes(),
                request.getExpiresAt(),
                createdBy
        );

        if (request.getRateLimitPerMinute() > 0) {
            apiKey.setRateLimitPerMinute(request.getRateLimitPerMinute());
        }

        ApiKey saved = apiKeyRepository.save(apiKey);

        return new ApiKeyCreatedResponse(
                saved.getId(),
                saved.getName(),
                saved.getKeyPrefix(),
                saved.getScopes(),
                saved.isActive(),
                saved.getExpiresAt(),
                saved.getLastUsedAt(),
                saved.getRateLimitPerMinute(),
                saved.getCreatedByName(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                rawKey
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getAllApiKeys() {
        return apiKeyRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyResponse getApiKeyById(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found with ID: " + id));
        return mapToResponse(apiKey);
    }

    @Override
    public ApiKeyResponse toggleApiKeyStatus(Long id, boolean active) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found with ID: " + id));
        apiKey.setActive(active);
        ApiKey saved = apiKeyRepository.save(apiKey);
        return mapToResponse(saved);
    }

    @Override
    public void deleteApiKey(Long id) {
        if (!apiKeyRepository.existsById(id)) {
            throw new ResourceNotFoundException("API Key not found with ID: " + id);
        }
        apiKeyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateScope(ApiKey apiKey, ApiKeyScope requiredScope) {
        if (apiKey == null || !apiKey.isValid()) {
            return false;
        }
        return apiKey.getScopes() != null && apiKey.getScopes().contains(requiredScope);
    }

    private ApiKeyResponse mapToResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getScopes(),
                apiKey.isActive(),
                apiKey.getExpiresAt(),
                apiKey.getLastUsedAt(),
                apiKey.getRateLimitPerMinute(),
                apiKey.getCreatedByName(),
                apiKey.getCreatedAt(),
                apiKey.getUpdatedAt()
        );
    }
}
