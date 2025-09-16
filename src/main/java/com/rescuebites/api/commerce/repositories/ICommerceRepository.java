package com.rescuebites.api.commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rescuebites.api.commerce.data.models.commerce;
import java.util.Optional;
import java.util.UUID;

public interface ICommerceRepository extends JpaRepository<commerce, UUID>{
    // Buscar por email para validar unicidad
    Optional<commerce> findByEmail(String email);
}
