package com.rescuebites.api.users.repositories;

import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ITokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByTokenId(UUID tokenId);

    /*
    @Query("SELECT t FROM tokens t WHERE t.user = :user ORDER BY t.createdAt DESC LIMIT 3")
    Optional<Token> findLatestByUser(@Param("user") User user);

     */

    //Cuenta los tokens creados por un usuario después de una fecha y hora específica
    long countByUserAndCreatedAtAfter(User user, LocalDateTime dateTime);

}

