package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.CommerceType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICommerceTypeRepository extends JpaRepository<CommerceType, UUID> {

    @Cacheable(value = "commerceTypes", key = "#name")
    Optional<CommerceType> findByName(CommerceTypeEnum name);
}

