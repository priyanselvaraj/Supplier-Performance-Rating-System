package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.NotificationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.*;
import com.supplier.sprsystem.repository.NotificationRepository;
import com.supplier.sprsystem.repository.RoleRepository;
import com.supplier.sprsystem.repository.UserNotificationPreferenceRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationPreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User mockUser;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@spr.com")
                .fullName("System Administrator")
                .build();

        mockNotification = Notification.builder()
                .id(100L)
                .user(mockUser)
                .title("Low Performance Alert")
                .message("Supplier ABC scored below 50%")
                .notificationType(NotificationType.ALERT)
                .priority(NotificationPriority.HIGH)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("NOTIF-01: Create notification persists entity and returns response")
    void testCreateNotification() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(preferenceRepository.findByUserIdAndNotificationType(1L, NotificationType.ALERT)).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        NotificationResponse response = notificationService.createNotification(
                1L, "Low Performance Alert", "Supplier ABC scored below 50%",
                NotificationType.ALERT, NotificationPriority.HIGH, "SUPPLIER", 5L
        );

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Low Performance Alert");
        assertThat(response.getPriority()).isEqualTo(NotificationPriority.HIGH);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("NOTIF-02: User can mark notification as read")
    void testMarkAsRead() {
        when(notificationRepository.findById(100L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        NotificationResponse response = notificationService.markAsRead(100L, 1L);

        assertThat(response).isNotNull();
        assertThat(mockNotification.isRead()).isTrue();
        assertThat(mockNotification.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("NOTIF-03: Retrieve paginated notifications for user")
    void testGetUserNotifications() {
        Page<Notification> page = new PageImpl<>(List.of(mockNotification));
        when(notificationRepository.filterNotifications(eq(1L), eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        PaginatedResponse<NotificationResponse> res = notificationService.getUserNotifications(1L, 0, 10, null, null);

        assertThat(res).isNotNull();
        assertThat(res.getContent()).hasSize(1);
        assertThat(res.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("NOTIF-04: Mark all as read updates user records")
    void testMarkAllAsRead() {
        doNothing().when(notificationRepository).markAllAsReadForUser(eq(1L), any(LocalDateTime.class));

        notificationService.markAllAsRead(1L);

        verify(notificationRepository, times(1)).markAllAsReadForUser(eq(1L), any(LocalDateTime.class));
    }
}
