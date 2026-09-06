package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WebhookDeliveryLog;
import com.supplier.sprsystem.model.entity.WebhookDeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookDeliveryLogRepository extends JpaRepository<WebhookDeliveryLog, Long> {

    List<WebhookDeliveryLog> findTop100ByOrderByDeliveredAtDesc();

    List<WebhookDeliveryLog> findBySubscriptionIdOrderByDeliveredAtDesc(Long subscriptionId);

    long countByStatus(WebhookDeliveryStatus status);
}
