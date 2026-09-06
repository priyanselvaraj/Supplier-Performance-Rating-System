package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.integration.ApiKeyCreateRequest;
import com.supplier.sprsystem.dto.integration.ApiKeyCreatedResponse;
import com.supplier.sprsystem.dto.integration.ApiKeyResponse;
import com.supplier.sprsystem.model.entity.ApiKey;
import com.supplier.sprsystem.model.entity.ApiKeyScope;
import com.supplier.sprsystem.repository.ApiKeyRepository;
import com.supplier.sprsystem.security.ApiKeyAuthenticationFilter;
import com.supplier.sprsystem.service.impl.ApiKeyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    private ApiKey sampleApiKey;

    @BeforeEach
    void setUp() {
        sampleApiKey = new ApiKey(
                "ERP Connector",
                "sprs_live_ab12",
                "samplehash1234567890",
                Set.of(ApiKeyScope.SUPPLIER_READ, ApiKeyScope.SUPPLIER_WRITE),
                LocalDateTime.now().plusMonths(6),
                "admin"
        );
        sampleApiKey.setId(1L);
    }

    @Test
    @DisplayName("Generate API Key creates secure key and returns raw value only once")
    void testGenerateApiKey() {
        when(apiKeyRepository.existsByName("ERP Connector")).thenReturn(false);
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey key = invocation.getArgument(0);
            key.setId(1L);
            return key;
        });

        ApiKeyCreateRequest request = new ApiKeyCreateRequest(
                "ERP Connector",
                Set.of(ApiKeyScope.SUPPLIER_READ, ApiKeyScope.SUPPLIER_WRITE),
                LocalDateTime.now().plusMonths(6),
                60
        );

        ApiKeyCreatedResponse response = apiKeyService.generateApiKey(request, "admin");

        assertNotNull(response);
        assertNotNull(response.getRawApiKey());
        assertTrue(response.getRawApiKey().startsWith("sprs_live_"));
        assertEquals("ERP Connector", response.getName());
        assertEquals(2, response.getScopes().size());
        assertTrue(response.isActive());
        verify(apiKeyRepository, times(1)).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("Generate API Key throws exception on duplicate name")
    void testGenerateApiKeyDuplicateName() {
        when(apiKeyRepository.existsByName("ERP Connector")).thenReturn(true);

        ApiKeyCreateRequest request = new ApiKeyCreateRequest(
                "ERP Connector",
                Set.of(ApiKeyScope.SUPPLIER_READ),
                null,
                60
        );

        assertThrows(IllegalArgumentException.class, () -> apiKeyService.generateApiKey(request, "admin"));
    }

    @Test
    @DisplayName("Get all API keys returns list of responses without raw secret")
    void testGetAllApiKeys() {
        when(apiKeyRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleApiKey));

        List<ApiKeyResponse> responses = apiKeyService.getAllApiKeys();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("ERP Connector", responses.get(0).getName());
        assertEquals("sprs_live_ab12", responses.get(0).getKeyPrefix());
    }

    @Test
    @DisplayName("Toggle API Key status updates active flag")
    void testToggleApiKeyStatus() {
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.of(sampleApiKey));
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(sampleApiKey);

        ApiKeyResponse response = apiKeyService.toggleApiKeyStatus(1L, false);

        assertNotNull(response);
        assertFalse(response.isActive());
        verify(apiKeyRepository, times(1)).save(sampleApiKey);
    }

    @Test
    @DisplayName("Validate scope returns true when key has scope")
    void testValidateScope() {
        assertTrue(apiKeyService.validateScope(sampleApiKey, ApiKeyScope.SUPPLIER_READ));
        assertTrue(apiKeyService.validateScope(sampleApiKey, ApiKeyScope.SUPPLIER_WRITE));
        assertFalse(apiKeyService.validateScope(sampleApiKey, ApiKeyScope.REPORT_READ));
    }

    @Test
    @DisplayName("SHA-256 key hashing is deterministic")
    void testKeyHashing() {
        String key = "sprs_live_1234567890abcdef";
        String hash1 = ApiKeyAuthenticationFilter.hashKey(key);
        String hash2 = ApiKeyAuthenticationFilter.hashKey(key);

        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }
}
