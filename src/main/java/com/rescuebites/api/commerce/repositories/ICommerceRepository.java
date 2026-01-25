package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.models.Commerce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICommerceRepository extends JpaRepository<Commerce, UUID> {

    Optional<Commerce> findByCommerceIdAndDeletedFalse(UUID commerceId);

    boolean existsByNameAndDeletedFalse(String name);
}