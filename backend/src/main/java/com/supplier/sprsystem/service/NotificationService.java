package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.NotificationPreferenceRequest;
import com.supplier.sprsystem.dto.response.NotificationPreferenceResponse;
import com.supplier.sprsystem.dto.response.NotificationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.NotificationPriority;
import com.supplier.sprsystem.model.entity.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(Long userId, String title, String message, NotificationType type, NotificationPriority priority, String resourceType, Long resourceId);

    void broadcastToRole(String roleName, String title, String message, NotificationType type, NotificationPriority priority, String resourceType, Long resourceId);

    PaginatedResponse<NotificationResponse> getUserNotifications(Long userId, int page, int size, Boolean unreadOnly, NotificationType type);

    List<NotificationResponse> getRecentNotifications(Long userId);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    long getUnreadCount(Long userId);

    NotificationResponse markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId, Long userId);

    List<NotificationPreferenceResponse> getUserPreferences(Long userId);

    List<NotificationPreferenceResponse> updateUserPreferences(Long userId, List<NotificationPreferenceRequest> preferences);

    SseEmitter subscribeSse(Long userId);
}
