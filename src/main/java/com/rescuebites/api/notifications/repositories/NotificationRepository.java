package com.rescuebites.api.notifications.repositories;

import com.rescuebites.api.notifications.data.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND (n.scheduledFor IS NULL OR n.scheduledFor <= :now)")
    List<Notification> findVisibleUnreadByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT n FROM Notification n WHERE n.scheduledFor IS NOT NULL AND n.scheduledFor <= :now AND n.isRead = false")
    List<Notification> findDueScheduledNotifications(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsRead(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") UUID id);
}