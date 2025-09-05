package com.rescuebites.api.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tokenId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    //Setea automáticamente la expiración cada vez que se genera un nuevo objeto de Token
    @Builder.Default
    private LocalDateTime tokenExpirationDate = LocalDateTime.now().plusHours(12);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}