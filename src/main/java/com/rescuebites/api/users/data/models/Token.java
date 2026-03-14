package com.rescuebites.api.users.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Token {

    @Id
    @Column(name = "tokenId")
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID tokenId = UUID.randomUUID();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    //Setea automáticamente la expiración cada vez que se genera un nuevo objeto de Token
    @Builder.Default
    private LocalDateTime tokenExpirationDate = LocalDateTime.now().plusHours(12);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
}