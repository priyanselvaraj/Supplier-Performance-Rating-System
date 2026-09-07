package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.NotificationPreferenceRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.NotificationPreferenceResponse;
import com.supplier.sprsystem.dto.response.NotificationResponse;
import com.supplier.sprsystem.dto.response.PaginatedResponse;
import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.EmailService;
import com.supplier.sprsystem.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification System", description = "In-app notifications, user preferences, and real-time SSE stream delivery")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationController(NotificationService notificationService, UserRepository userRepository, EmailService emailService) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated user notifications with optional filtering")
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) NotificationType type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        PaginatedResponse<NotificationResponse> response = notificationService.getUserNotifications(userId, page, size, unreadOnly, type);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", response));
    }

    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get 10 most recent notifications for header bell dropdown")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getRecentNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        List<NotificationResponse> response = notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Recent notifications retrieved successfully", response));
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all unread notifications for current user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        List<NotificationResponse> response = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved successfully", response));
    }

    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unread notifications count for bell badge")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("unreadCount", count);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", result));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark specific notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        NotificationResponse response = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read for current user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete specific notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    @GetMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user notification channel preferences")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceResponse>>> getPreferences(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        List<NotificationPreferenceResponse> response = notificationService.getUserPreferences(userId);
        return ResponseEntity.ok(ApiResponse.success("User preferences retrieved successfully", response));
    }

    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update user notification channel preferences")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceResponse>>> updatePreferences(
            @RequestBody List<NotificationPreferenceRequest> preferences,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserId(userDetails);
        List<NotificationPreferenceResponse> response = notificationService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok(ApiResponse.success("User preferences updated successfully", response));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Subscribe to live real-time Server-Sent Events (SSE) notification stream")
    public SseEmitter subscribeStream(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return notificationService.subscribeSse(userId);
    }

    @PostMapping("/test-email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send test notification email to a designated email/Gmail address")
    public ResponseEntity<ApiResponse<String>> sendTestEmail(
            @RequestParam(required = false) String to,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String targetEmail = (to != null && !to.trim().isEmpty())
                ? to.trim()
                : (userDetails != null && userDetails.getUsername() != null && userDetails.getUsername().contains("@") ? userDetails.getUsername() : "test@example.com");

        if (targetEmail.contains("@")) {
            User user = userRepository.findByEmail(targetEmail)
                    .or(() -> userDetails != null ? userRepository.findByUsername(userDetails.getUsername()) : Optional.empty())
                    .orElseGet(() -> User.builder().username(targetEmail).email(targetEmail).fullName("SPRS User").build());

            emailService.sendGeneralNotificationEmail(
                    user,
                    "SPRS Live Email Notification Test",
                    "This is a test notification message from the Supplier Performance Rating System. Your email channel is configured and actively receiving messages!",
                    NotificationType.SYSTEM
            );
            return ResponseEntity.ok(ApiResponse.success("Test email dispatched to " + targetEmail, targetEmail));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid email address: " + targetEmail));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) return 1L;
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);
        return user != null ? user.getId() : 1L;
    }
}
