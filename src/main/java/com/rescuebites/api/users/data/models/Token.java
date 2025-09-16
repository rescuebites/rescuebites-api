package com.rescuebites.api.users.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @Column(name = "tokenId")
    private UUID tokenId = UUID.randomUUID();;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    //Setea automáticamente la expiración cada vez que se genera un nuevo objeto de Token
    @Builder.Default
    private LocalDateTime tokenExpirationDate = LocalDateTime.now().plusHours(12);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private LocalDateTime expiryDate;

   // @JoinColumn(name = "comercio_id", referencedColumnName = "id")
    // private commerce commerce;


}