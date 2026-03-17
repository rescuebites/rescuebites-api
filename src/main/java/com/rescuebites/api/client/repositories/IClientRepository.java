package com.rescuebites.api.client.repositories;

import com.rescuebites.api.client.data.models.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IClientRepository extends JpaRepository<Client, UUID> {

    @EntityGraph(attributePaths = {"preferences", "user"})
    Optional<Client> findByClientIdAndDeletedFalse(UUID clientId);

    @Query("SELECT c FROM clients c WHERE c.user.userId = :userId AND c.deleted = false")
    Optional<Client> findByUserId(@Param("userId") UUID userId);
}
