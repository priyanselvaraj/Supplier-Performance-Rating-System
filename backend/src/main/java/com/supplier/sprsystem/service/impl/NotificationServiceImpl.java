package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.NotificationPreferenceRequest;
import com.supplier.sprsystem.dto.response.NotificationPreferenceResponse;
import com.supplier.sprsystem.dto.response.NotificationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.NotificationRepository;
import com.supplier.sprsystem.repository.RoleRepository;
import com.supplier.sprsystem.repository.UserNotificationPreferenceRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final Map<Long, List<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                  UserNotificationPreferenceRepository preferenceRepository,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public NotificationResponse createNotification(Long userId, String title, String message, NotificationType type, NotificationPriority priority, String resourceType, Long resourceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check user preferences
        Optional<UserNotificationPreference> prefOpt = preferenceRepository.findByUserIdAndNotificationType(userId, type);
        if (prefOpt.isPresent() && !prefOpt.get().isInAppEnabled()) {
            logger.debug("In-app notification of type {} skipped for user {} due to user preferences", type, userId);
            return null;
        }

        // Duplicate prevention: check within last 6 hours
        if (resourceType != null && resourceId != null) {
            LocalDateTime sixHoursAgo = LocalDateTime.now().minusHours(6);
            if (notificationRepository.existsByUserIdAndTitleAndRelatedResourceTypeAndRelatedResourceIdAndCreatedAtAfter(
                    userId, title, resourceType, resourceId, sixHoursAgo)) {
                logger.debug("Duplicate notification prevented for user {}, title: {}", userId, title);
                return null;
            }
        }

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .priority(priority != null ? priority : NotificationPriority.MEDIUM)
                .relatedResourceType(resourceType)
                .relatedResourceId(resourceId)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);

        // Real-time dispatch via SSE
        dispatchSse(userId, response);

        return response;
    }

    @Override
    public void broadcastToRole(String roleName, String title, String message, NotificationType type, NotificationPriority priority, String resourceType, Long resourceId) {
        String searchRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().name().equals(searchRole)))
                .collect(Collectors.toList());

        for (User u : users) {
            try {
                createNotification(u.getId(), title, message, type, priority, resourceType, resourceId);
            } catch (Exception e) {
                logger.error("Error creating broadcast notification for user {}", u.getId(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<NotificationResponse> getUserNotifications(Long userId, int page, int size, Boolean unreadOnly, NotificationType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifPage = notificationRepository.filterNotifications(userId, unreadOnly, type, pageable);

        List<NotificationResponse> content = notifPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                notifPage.getNumber(),
                notifPage.getSize(),
                notifPage.getTotalElements(),
                notifPage.getTotalPages(),
                notifPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecentNotifications(Long userId) {
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notif.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to notification");
        }

        notif.setRead(true);
        notif.setReadAt(LocalDateTime.now());
        Notification updated = notificationRepository.save(notif);
        return mapToResponse(updated);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId, LocalDateTime.now());
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notif.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to notification");
        }

        notificationRepository.delete(notif);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getUserPreferences(Long userId) {
        List<UserNotificationPreference> existing = preferenceRepository.findByUserId(userId);
        Map<NotificationType, UserNotificationPreference> map = existing.stream()
                .collect(Collectors.toMap(UserNotificationPreference::getNotificationType, p -> p));

        List<NotificationPreferenceResponse> result = new ArrayList<>();
        for (NotificationType type : NotificationType.values()) {
            UserNotificationPreference pref = map.get(type);
            boolean inApp = pref != null ? pref.isInAppEnabled() : true;
            boolean email = pref != null ? pref.isEmailEnabled() : true;

            result.add(NotificationPreferenceResponse.builder()
                    .id(pref != null ? pref.getId() : null)
                    .notificationType(type)
                    .displayName(type.getDisplayName())
                    .inAppEnabled(inApp)
                    .emailEnabled(email)
                    .build());
        }
        return result;
    }

    @Override
    public List<NotificationPreferenceResponse> updateUserPreferences(Long userId, List<NotificationPreferenceRequest> preferences) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        for (NotificationPreferenceRequest req : preferences) {
            Optional<UserNotificationPreference> existingOpt = preferenceRepository.findByUserIdAndNotificationType(userId, req.getNotificationType());
            UserNotificationPreference pref;
            if (existingOpt.isPresent()) {
                pref = existingOpt.get();
                pref.setInAppEnabled(req.isInAppEnabled());
                pref.setEmailEnabled(req.isEmailEnabled());
            } else {
                pref = UserNotificationPreference.builder()
                        .user(user)
                        .notificationType(req.getNotificationType())
                        .inAppEnabled(req.isInAppEnabled())
                        .emailEnabled(req.isEmailEnabled())
                        .build();
            }
            preferenceRepository.save(pref);
        }

        return getUserPreferences(userId);
    }

    @Override
    public SseEmitter subscribeSse(Long userId) {
        SseEmitter emitter = new SseEmitter(180_000L); // 3 minutes connection lifetime

        sseEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to SPRS Real-Time Notification Stream"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
        }

        return emitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> list = sseEmitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                sseEmitters.remove(userId);
            }
        }
    }

    private void dispatchSse(Long userId, NotificationResponse notification) {
        List<SseEmitter> emitters = sseEmitters.get(userId);
        if (emitters != null && !emitters.isEmpty()) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("NOTIFICATION")
                            .data(notification));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
            emitters.removeAll(deadEmitters);
        }
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUser().getId())
                .username(n.getUser().getUsername())
                .title(n.getTitle())
                .message(n.getMessage())
                .notificationType(n.getNotificationType())
                .priority(n.getPriority())
                .relatedResourceType(n.getRelatedResourceType())
                .relatedResourceId(n.getRelatedResourceId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
