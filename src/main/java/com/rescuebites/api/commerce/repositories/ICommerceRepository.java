package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.models.Commerce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ICommerceRepository extends JpaRepository<Commerce, UUID> {

    Optional<Commerce> findByCommerceIdAndActiveTrue(UUID commerceId);
}
