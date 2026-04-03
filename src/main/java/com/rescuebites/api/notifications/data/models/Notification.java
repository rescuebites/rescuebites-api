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
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private String role; // CLIENT / COMMERCE

    private String type; // ORDER_STATUS, NEW_ORDER, etc

    private String title;

    private String message;

    @Column(columnDefinition = "TEXT")
    private String data; // JSON serializado

    private boolean read;

    private LocalDateTime createdAt;
}
