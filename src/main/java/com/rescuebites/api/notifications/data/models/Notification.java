package com.rescuebites.api.notifications.data.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @Column(name = "notification_id")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID userId;

    private String role;

    private String type;

    private String title;

    private String message;

    private String eventId;

    private String registerId;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}