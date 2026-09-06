package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.integration.IntegrationHealthSummaryDto;
import com.supplier.sprsystem.model.entity.IntegrationSyncHistory;
import com.supplier.sprsystem.model.entity.SyncStatus;
import com.supplier.sprsystem.model.entity.WebhookDeliveryLog;
import com.supplier.sprsystem.model.entity.WebhookDeliveryStatus;
import com.supplier.sprsystem.repository.ApiKeyRepository;
import com.supplier.sprsystem.repository.IntegrationSyncHistoryRepository;
import com.supplier.sprsystem.repository.WebhookDeliveryLogRepository;
import com.supplier.sprsystem.repository.WebhookSubscriptionRepository;
import com.supplier.sprsystem.service.IntegrationMonitoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IntegrationMonitoringServiceImpl implements IntegrationMonitoringService {

    private final ApiKeyRepository apiKeyRepository;
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final WebhookDeliveryLogRepository deliveryLogRepository;
    private final IntegrationSyncHistoryRepository syncHistoryRepository;

    public IntegrationMonitoringServiceImpl(ApiKeyRepository apiKeyRepository,
                                            WebhookSubscriptionRepository webhookSubscriptionRepository,
                                            WebhookDeliveryLogRepository deliveryLogRepository,
                                            IntegrationSyncHistoryRepository syncHistoryRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.deliveryLogRepository = deliveryLogRepository;
        this.syncHistoryRepository = syncHistoryRepository;
    }

    @Override
    public IntegrationHealthSummaryDto getIntegrationHealthSummary() {
        long totalKeys = apiKeyRepository.count();
        long activeKeys = apiKeyRepository.countByActiveTrue();

        long totalWebhooks = webhookSubscriptionRepository.count();
        long activeWebhooks = webhookSubscriptionRepository.countByActiveTrue();

        long successfulDeliveries = deliveryLogRepository.countByStatus(WebhookDeliveryStatus.SUCCESS);
        long failedDeliveries = deliveryLogRepository.countByStatus(WebhookDeliveryStatus.FAILED);
        long totalDeliveries = successfulDeliveries + failedDeliveries;

        double deliverySuccessRate = totalDeliveries > 0
                ? Math.round(((double) successfulDeliveries / totalDeliveries) * 1000.0) / 10.0
                : 100.0;

        long successfulSyncs = syncHistoryRepository.countByStatus(SyncStatus.SUCCESS);
        long partialSyncs = syncHistoryRepository.countByStatus(SyncStatus.PARTIAL_SUCCESS);
        long failedSyncs = syncHistoryRepository.countByStatus(SyncStatus.FAILED);
        long totalSyncs = successfulSyncs + partialSyncs + failedSyncs;

        double syncSuccessRate = totalSyncs > 0
                ? Math.round(((double) (successfulSyncs + partialSyncs) / totalSyncs) * 1000.0) / 10.0
                : 100.0;

        List<IntegrationSyncHistory> recentSyncs = syncHistoryRepository.findTop20ByOrderByStartedAtDesc();
        LocalDateTime lastSyncTime = !recentSyncs.isEmpty() ? recentSyncs.get(0).getStartedAt() : null;

        List<WebhookDeliveryLog> recentLogs = deliveryLogRepository.findTop100ByOrderByDeliveredAtDesc();
        LocalDateTime lastWebhookTime = !recentLogs.isEmpty() ? recentLogs.get(0).getDeliveredAt() : null;

        return new IntegrationHealthSummaryDto(
                activeKeys,
                totalKeys,
                activeWebhooks,
                totalWebhooks,
                totalDeliveries,
                successfulDeliveries,
                failedDeliveries,
                deliverySuccessRate,
                totalSyncs,
                successfulSyncs + partialSyncs,
                syncSuccessRate,
                lastSyncTime,
                lastWebhookTime
        );
    }
}
