package com.supplier.sprsystem.repository;

import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.model.entity.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, Long> {

    List<UserNotificationPreference> findByUserId(Long userId);

    Optional<UserNotificationPreference> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);
}
