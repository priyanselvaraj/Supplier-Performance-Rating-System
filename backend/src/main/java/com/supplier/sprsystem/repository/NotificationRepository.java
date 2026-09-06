package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.Notification;
import com.supplier.sprsystem.model.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND (:isRead IS NULL OR n.isRead = :isRead) " +
            "AND (:type IS NULL OR n.notificationType = :type) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> filterNotifications(
            @Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            @Param("type") NotificationType type,
            Pageable pageable
    );

    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    boolean existsByUserIdAndTitleAndRelatedResourceTypeAndRelatedResourceIdAndCreatedAtAfter(
            Long userId, String title, String resourceType, Long resourceId, LocalDateTime after
    );
}
