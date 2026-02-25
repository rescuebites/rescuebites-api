package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICommerceRepository extends JpaRepository<Commerce, UUID> {

    @Cacheable(value = "commerceById", key = "#commerceId")
    Optional<Commerce> findByCommerceIdAndDeletedFalse(UUID commerceId);

    boolean existsByNameAndDeletedFalse(String name);

    Page<Commerce> findByDeletedFalse(Pageable pageable);

    @Query("SELECT DISTINCT c FROM commerces c " +
            "JOIN c.commerceTypes ct " +
            "WHERE ct.name = :commerceType AND c.deleted = false")
    Page<Commerce> findActiveByCommerceType(
            @Param("commerceType") CommerceTypeEnum commerceType,
            Pageable pageable
    );

    @Query("SELECT c FROM commerces c WHERE c.deleted = false " +
            "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY CASE " +
            "  WHEN LOWER(c.name) LIKE LOWER(CONCAT(:query, '%')) THEN 0 " +
            "  ELSE 1 " +
            "END, c.name ASC")
    Page<Commerce> findActiveByNameContaining(
            @Param("query") String query,
            Pageable pageable
    );

    @Query("SELECT c FROM commerces c WHERE c.deleted = false " +
            "AND c.mercadoPagoWebhookSecret IS NOT NULL " +
            "AND c.mercadoPagoWebhookSecret <> ''")
    List<Commerce> findAllWithWebhookSecret();
}