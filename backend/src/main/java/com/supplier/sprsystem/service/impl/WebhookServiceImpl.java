package com.supplier.sprsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supplier.sprsystem.dto.integration.WebhookDeliveryLogResponse;
import com.supplier.sprsystem.dto.integration.WebhookPayload;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionRequest;
import com.supplier.sprsystem.dto.integration.WebhookSubscriptionResponse;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.WebhookDeliveryLog;
import com.supplier.sprsystem.model.entity.WebhookDeliveryStatus;
import com.supplier.sprsystem.model.entity.WebhookEventType;
import com.supplier.sprsystem.model.entity.WebhookSubscription;
import com.supplier.sprsystem.repository.WebhookDeliveryLogRepository;
import com.supplier.sprsystem.repository.WebhookSubscriptionRepository;
import com.supplier.sprsystem.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WebhookServiceImpl implements WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryLogRepository deliveryLogRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public WebhookServiceImpl(WebhookSubscriptionRepository subscriptionRepository,
                              WebhookDeliveryLogRepository deliveryLogRepository,
                              RestClient restClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.deliveryLogRepository = deliveryLogRepository;
        this.restClient = restClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public WebhookSubscriptionResponse createSubscription(WebhookSubscriptionRequest request) {
        String secretToken = request.getSecretToken();
        if (!StringUtils.hasText(secretToken)) {
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            secretToken = "whsec_" + HexFormat.of().formatHex(randomBytes);
        }

        WebhookSubscription subscription = new WebhookSubscription(
                request.getName(),
                request.getTargetUrl(),
                secretToken,
                request.getEventTypes()
        );

        WebhookSubscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookSubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WebhookSubscriptionResponse getSubscriptionById(Long id) {
        WebhookSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook subscription not found with ID: " + id));
        return mapToResponse(subscription);
    }

    @Override
    public WebhookSubscriptionResponse updateSubscription(Long id, WebhookSubscriptionRequest request) {
        WebhookSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook subscription not found with ID: " + id));

        subscription.setName(request.getName());
        subscription.setTargetUrl(request.getTargetUrl());
        subscription.setEventTypes(request.getEventTypes());

        if (StringUtils.hasText(request.getSecretToken())) {
            subscription.setSecretToken(request.getSecretToken());
        }

        WebhookSubscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Override
    public WebhookSubscriptionResponse toggleSubscriptionStatus(Long id, boolean active) {
        WebhookSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook subscription not found with ID: " + id));
        subscription.setActive(active);
        WebhookSubscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Override
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Webhook subscription not found with ID: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    @Override
    public void dispatchEvent(WebhookEventType eventType, Object eventData) {
        List<WebhookSubscription> activeSubscriptions = subscriptionRepository.findByActiveTrue();

        WebhookPayload<Object> payloadObj = new WebhookPayload<>(eventType, eventData);

        for (WebhookSubscription sub : activeSubscriptions) {
            if (sub.getEventTypes() != null && sub.getEventTypes().contains(eventType)) {
                deliverWebhook(sub, eventType, payloadObj);
            }
        }
    }

    @Override
    public WebhookDeliveryLogResponse sendTestPing(Long subscriptionId) {
        WebhookSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook subscription not found with ID: " + subscriptionId));

        WebhookPayload<String> pingPayload = new WebhookPayload<>(
                WebhookEventType.TEST_PING,
                "SPRS Webhook connectivity test from system at " + LocalDateTime.now()
        );

        WebhookDeliveryLog log = deliverWebhook(subscription, WebhookEventType.TEST_PING, pingPayload);
        return mapToLogResponse(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogResponse> getRecentDeliveryLogs() {
        return deliveryLogRepository.findTop100ByOrderByDeliveredAtDesc()
                .stream()
                .map(this::mapToLogResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookDeliveryLogResponse> getLogsBySubscription(Long subscriptionId) {
        return deliveryLogRepository.findBySubscriptionIdOrderByDeliveredAtDesc(subscriptionId)
                .stream()
                .map(this::mapToLogResponse)
                .collect(Collectors.toList());
    }

    private WebhookDeliveryLog deliverWebhook(WebhookSubscription subscription, WebhookEventType eventType, WebhookPayload<?> payload) {
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.setSubscription(subscription);
        log.setSubscriptionName(subscription.getName());
        log.setEventType(eventType);
        log.setTargetUrl(subscription.getTargetUrl());
        log.setAttemptCount(1);
        log.setDeliveredAt(LocalDateTime.now());

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
            log.setPayload(jsonPayload);
        } catch (Exception e) {
            log.setStatus(WebhookDeliveryStatus.FAILED);
            log.setErrorMessage("Failed to serialize webhook payload: " + e.getMessage());
            subscription.recordFailure("Serialization error: " + e.getMessage());
            subscriptionRepository.save(subscription);
            return deliveryLogRepository.save(log);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String signature = calculateHmacSha256(jsonPayload, subscription.getSecretToken());

        long start = System.currentTimeMillis();
        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(subscription.getTargetUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-SPRS-Event", eventType.name())
                    .header("X-SPRS-Timestamp", timestamp)
                    .header("X-SPRS-Signature", "sha256=" + signature)
                    .header("User-Agent", "SPRS-Webhook-Service/1.0")
                    .body(jsonPayload)
                    .retrieve()
                    .toEntity(String.class);

            long duration = System.currentTimeMillis() - start;
            log.setDurationMs(duration);
            log.setResponseStatus(response.getStatusCode().value());
            log.setResponseBody(truncateBody(response.getBody()));

            if (response.getStatusCode().is2xxSuccessful()) {
                log.setStatus(WebhookDeliveryStatus.SUCCESS);
                subscription.recordSuccess();
            } else {
                log.setStatus(WebhookDeliveryStatus.FAILED);
                log.setErrorMessage("HTTP Error " + response.getStatusCode().value());
                subscription.recordFailure("HTTP Error " + response.getStatusCode().value());
            }
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            log.setDurationMs(duration);
            log.setStatus(WebhookDeliveryStatus.FAILED);
            log.setErrorMessage(ex.getMessage());
            subscription.recordFailure(ex.getMessage());
            logger.warn("Failed to deliver webhook to URL {}: {}", subscription.getTargetUrl(), ex.getMessage());
        }

        subscriptionRepository.save(subscription);
        return deliveryLogRepository.save(log);
    }

    public static String calculateHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to calculate HMAC-SHA256 signature", e);
        }
    }

    private String truncateBody(String body) {
        if (body == null) return null;
        return body.length() > 2000 ? body.substring(0, 2000) + "... [truncated]" : body;
    }

    private WebhookSubscriptionResponse mapToResponse(WebhookSubscription sub) {
        return new WebhookSubscriptionResponse(
                sub.getId(),
                sub.getName(),
                sub.getTargetUrl(),
                sub.getSecretToken(),
                sub.getEventTypes(),
                sub.isActive(),
                sub.getFailureCount(),
                sub.getLastSuccessAt(),
                sub.getLastFailureAt(),
                sub.getLastErrorMessage(),
                sub.getCreatedAt(),
                sub.getUpdatedAt()
        );
    }

    private WebhookDeliveryLogResponse mapToLogResponse(WebhookDeliveryLog log) {
        return new WebhookDeliveryLogResponse(
                log.getId(),
                log.getSubscription() != null ? log.getSubscription().getId() : null,
                log.getSubscriptionName(),
                log.getEventType(),
                log.getTargetUrl(),
                log.getPayload(),
                log.getResponseStatus(),
                log.getResponseBody(),
                log.getDurationMs(),
                log.getStatus(),
                log.getAttemptCount(),
                log.getErrorMessage(),
                log.getDeliveredAt()
        );
    }
}
