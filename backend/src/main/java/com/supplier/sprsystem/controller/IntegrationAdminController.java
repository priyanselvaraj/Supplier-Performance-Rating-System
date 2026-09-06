package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.integration.*;
import com.supplier.sprsystem.model.entity.IntegrationSyncHistory;
import com.supplier.sprsystem.service.ApiKeyService;
import com.supplier.sprsystem.service.IntegrationMonitoringService;
import com.supplier.sprsystem.service.SupplierSyncService;
import com.supplier.sprsystem.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/integrations")
@PreAuthorize("hasRole('ADMIN')")
public class IntegrationAdminController {

    private final ApiKeyService apiKeyService;
    private final WebhookService webhookService;
    private final SupplierSyncService supplierSyncService;
    private final IntegrationMonitoringService monitoringService;

    public IntegrationAdminController(ApiKeyService apiKeyService,
                                      WebhookService webhookService,
                                      SupplierSyncService supplierSyncService,
                                      IntegrationMonitoringService monitoringService) {
        this.apiKeyService = apiKeyService;
        this.webhookService = webhookService;
        this.supplierSyncService = supplierSyncService;
        this.monitoringService = monitoringService;
    }

    // ==========================================
    // 1. Integration Health Overview
    // ==========================================
    @GetMapping("/health")
    public ResponseEntity<IntegrationHealthSummaryDto> getHealthSummary() {
        return ResponseEntity.ok(monitoringService.getIntegrationHealthSummary());
    }

    // ==========================================
    // 2. API Key Management
    // ==========================================
    @GetMapping("/api-keys")
    public ResponseEntity<List<ApiKeyResponse>> getAllApiKeys() {
        return ResponseEntity.ok(apiKeyService.getAllApiKeys());
    }

    @PostMapping("/api-keys")
    public ResponseEntity<ApiKeyCreatedResponse> createApiKey(
            @Valid @RequestBody ApiKeyCreateRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication != null ? authentication.getName() : "ADMIN";
        ApiKeyCreatedResponse response = apiKeyService.generateApiKey(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api-keys/{id}")
    public ResponseEntity<ApiKeyResponse> getApiKeyById(@PathVariable Long id) {
        return ResponseEntity.ok(apiKeyService.getApiKeyById(id));
    }

    @PatchMapping("/api-keys/{id}/status")
    public ResponseEntity<ApiKeyResponse> toggleApiKeyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap
    ) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(apiKeyService.toggleApiKeyStatus(id, active));
    }

    @DeleteMapping("/api-keys/{id}")
    public ResponseEntity<Map<String, String>> deleteApiKey(@PathVariable Long id) {
        apiKeyService.deleteApiKey(id);
        return ResponseEntity.ok(Map.of("message", "API Key successfully deleted"));
    }

    // ==========================================
    // 3. Webhook Subscriptions
    // ==========================================
    @GetMapping("/webhooks")
    public ResponseEntity<List<WebhookSubscriptionResponse>> getAllWebhooks() {
        return ResponseEntity.ok(webhookService.getAllSubscriptions());
    }

    @PostMapping("/webhooks")
    public ResponseEntity<WebhookSubscriptionResponse> createWebhook(
            @Valid @RequestBody WebhookSubscriptionRequest request
    ) {
        WebhookSubscriptionResponse response = webhookService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/webhooks/{id}")
    public ResponseEntity<WebhookSubscriptionResponse> getWebhookById(@PathVariable Long id) {
        return ResponseEntity.ok(webhookService.getSubscriptionById(id));
    }

    @PutMapping("/webhooks/{id}")
    public ResponseEntity<WebhookSubscriptionResponse> updateWebhook(
            @PathVariable Long id,
            @Valid @RequestBody WebhookSubscriptionRequest request
    ) {
        return ResponseEntity.ok(webhookService.updateSubscription(id, request));
    }

    @PatchMapping("/webhooks/{id}/status")
    public ResponseEntity<WebhookSubscriptionResponse> toggleWebhookStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusMap
    ) {
        boolean active = statusMap.getOrDefault("active", true);
        return ResponseEntity.ok(webhookService.toggleSubscriptionStatus(id, active));
    }

    @DeleteMapping("/webhooks/{id}")
    public ResponseEntity<Map<String, String>> deleteWebhook(@PathVariable Long id) {
        webhookService.deleteSubscription(id);
        return ResponseEntity.ok(Map.of("message", "Webhook subscription successfully deleted"));
    }

    @PostMapping("/webhooks/{id}/test")
    public ResponseEntity<WebhookDeliveryLogResponse> sendTestPing(@PathVariable Long id) {
        return ResponseEntity.ok(webhookService.sendTestPing(id));
    }

    // ==========================================
    // 4. Webhook Delivery Logs
    // ==========================================
    @GetMapping("/delivery-logs")
    public ResponseEntity<List<WebhookDeliveryLogResponse>> getRecentDeliveryLogs() {
        return ResponseEntity.ok(webhookService.getRecentDeliveryLogs());
    }

    @GetMapping("/delivery-logs/subscription/{subscriptionId}")
    public ResponseEntity<List<WebhookDeliveryLogResponse>> getLogsBySubscription(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(webhookService.getLogsBySubscription(subscriptionId));
    }

    // ==========================================
    // 5. Supplier Synchronization Engine
    // ==========================================
    @PostMapping("/sync/suppliers")
    public ResponseEntity<SupplierSyncResponse> syncSuppliers(
            @Valid @RequestBody SupplierSyncRequest request,
            Authentication authentication
    ) {
        String executedBy = authentication != null ? authentication.getName() : "ADMIN";
        SupplierSyncResponse response = supplierSyncService.syncSuppliers(request, executedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sync/history")
    public ResponseEntity<List<IntegrationSyncHistory>> getSyncHistories() {
        return ResponseEntity.ok(supplierSyncService.getRecentSyncHistories());
    }

    @GetMapping("/sync/history/{id}")
    public ResponseEntity<IntegrationSyncHistory> getSyncHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierSyncService.getSyncHistoryById(id));
    }
}
