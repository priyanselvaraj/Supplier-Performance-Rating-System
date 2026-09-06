package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {

    List<WebhookSubscription> findByActiveTrue();

    List<WebhookSubscription> findAllByOrderByCreatedAtDesc();

    long countByActiveTrue();
}
