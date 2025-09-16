package com.rescuebites.api.client.repositories;

import com.rescuebites.api.client.data.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IClientRepository extends JpaRepository<Client, UUID> {
}
