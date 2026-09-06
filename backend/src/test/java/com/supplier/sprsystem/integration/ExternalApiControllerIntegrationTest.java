package com.supplier.sprsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.external.ExternalSupplierCreateRequest;
import com.supplier.sprsystem.dto.integration.ApiKeyCreateRequest;
import com.supplier.sprsystem.dto.integration.SupplierSyncItem;
import com.supplier.sprsystem.dto.integration.SupplierSyncRequest;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionRequest;
import com.supplier.sprsystem.model.entity.ApiKeyScope;
import com.supplier.sprsystem.model.entity.SyncMode;
import com.supplier.sprsystem.model.entity.WebhookEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ExternalApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN", "SCOPE_SUPPLIER_READ"})
    @DisplayName("EXT-IT-01: Authenticated client can retrieve external suppliers list")
    void testGetExternalSuppliers() throws Exception {
        mockMvc.perform(get("/api/v1/external/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN", "SCOPE_REPORT_READ"})
    @DisplayName("EXT-IT-02: External client can retrieve executive report summary")
    void testGetExternalReportSummary() throws Exception {
        mockMvc.perform(get("/api/v1/external/reports/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSuppliers", notNullValue()))
                .andExpect(jsonPath("$.averagePerformanceScore", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN", "SCOPE_SUPPLIER_WRITE"})
    @DisplayName("EXT-IT-03: External client can create new supplier")
    void testCreateExternalSupplier() throws Exception {
        String uniqueCode = "EXT-SUP-" + System.currentTimeMillis();
        ExternalSupplierCreateRequest request = new ExternalSupplierCreateRequest(
                uniqueCode,
                "External Tech Partner Inc",
                "Jane Doe",
                uniqueCode.toLowerCase() + "@partner.com",
                "+1-555-9876",
                "100 Tech Blvd, Austin, TX",
                "https://partner.com",
                "Electronics & Hardware",
                "ACTIVE"
        );

        mockMvc.perform(post("/api/v1/external/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierCode", is(uniqueCode)))
                .andExpect(jsonPath("$.name", is("External Tech Partner Inc")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADM-IT-01: Admin can retrieve integration health summary")
    void testGetIntegrationHealth() throws Exception {
        mockMvc.perform(get("/api/v1/admin/integrations/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeApiKeysCount", notNullValue()))
                .andExpect(jsonPath("$.deliverySuccessRate", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADM-IT-02: Admin can manage API keys and webhooks")
    void testAdminApiKeyAndWebhookLifecycle() throws Exception {
        // 1. Create API key
        ApiKeyCreateRequest keyReq = new ApiKeyCreateRequest(
                "Test Integration Key " + System.currentTimeMillis(),
                Set.of(ApiKeyScope.SUPPLIER_READ, ApiKeyScope.REPORT_READ),
                null,
                100
        );

        mockMvc.perform(post("/api/v1/admin/integrations/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(keyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rawApiKey", notNullValue()))
                .andExpect(jsonPath("$.keyPrefix", notNullValue()));

        // 2. Create Webhook
        WebhookSubscriptionRequest whReq = new WebhookSubscriptionRequest(
                "Test Webhook " + System.currentTimeMillis(),
                "https://webhook.site/test-receiver",
                Set.of(WebhookEventType.SUPPLIER_CREATED),
                null
        );

        mockMvc.perform(post("/api/v1/admin/integrations/webhooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(whReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.secretToken", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADM-IT-03: Admin can trigger supplier batch synchronization")
    void testAdminSupplierSync() throws Exception {
        String code = "SYNC-SUP-" + System.currentTimeMillis();
        SupplierSyncItem item = new SupplierSyncItem(
                code,
                "Synced Global Logistics",
                "Mark Logistics",
                code.toLowerCase() + "@logistics.com",
                "+1-800-4444",
                "Logistics Hub, Chicago",
                "https://logistics.com",
                "Logistics & Shipping",
                "ACTIVE"
        );

        SupplierSyncRequest request = new SupplierSyncRequest(
                "Oracle NetSuite ERP",
                null,
                SyncMode.UPSERT,
                List.of(item)
        );

        mockMvc.perform(post("/api/v1/admin/integrations/sync/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords", is(1)))
                .andExpect(jsonPath("$.createdCount", is(1)))
                .andExpect(jsonPath("$.status", is("SUCCESS")));
    }
}
