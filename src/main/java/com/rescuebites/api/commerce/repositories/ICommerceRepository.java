package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.projections.CommerceWebhookSecretProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ICommerceRepository extends JpaRepository<Commerce, UUID> {

    Optional<Commerce> findByCommerceIdAndDeletedFalse(UUID commerceId);

    @Query("SELECT c FROM commerces c " +
            "LEFT JOIN FETCH c.images " +
            "WHERE c.commerceId = :commerceId AND c.deleted = false")
    Optional<Commerce> findByIdWithDetails(@Param("commerceId") UUID commerceId);

    @Query("SELECT c FROM commerces c WHERE c.deleted = false AND c.normalizedLocality = :normalizedLocality")
    Page<Commerce> findByDeletedFalseAndLocalityIgnoreCase(
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM commerces c " +
            "JOIN c.commerceTypes ct " +
            "WHERE ct.name = :commerceType AND c.deleted = false " +
            "AND c.normalizedLocality = :normalizedLocality")
    Page<Commerce> findActiveByCommerceTypeAndLocality(
            @Param("commerceType") CommerceTypeEnum commerceType,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @Query("SELECT c.commerceId AS commerceId, c.mercadoPagoWebhookSecret AS mercadoPagoWebhookSecret " +
            "FROM commerces c WHERE c.deleted = false  " +
            "AND c.mercadoPagoWebhookSecret IS NOT NULL " +
            "AND c.mercadoPagoWebhookSecret <> ''")
    List<CommerceWebhookSecretProjection> findAllWithWebhookSecret();

    @Query("SELECT c FROM commerces c WHERE c.deleted = false AND c.normalizedName = :normalizedName")
    Optional<Commerce> findActiveByExactNormalizedName(@Param("normalizedName") String normalizedName);

    @Query("SELECT c FROM commerces c " +
            "WHERE c.deleted = false " +
            "AND c.normalizedName LIKE CONCAT('%', :query, '%') " +
            "ORDER BY CASE " +
            "  WHEN c.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "  ELSE 1 " +
            "END, c.name ASC")
    Page<Commerce> findActiveByNameContainingRanked(
            @Param("query") String query,
            Pageable pageable
    );

    @Query("SELECT c FROM commerces c " +
            "WHERE c.deleted = false " +
            "AND c.normalizedName LIKE CONCAT('%', :query, '%') " +
            "AND c.normalizedLocality = :normalizedLocality " +
            "ORDER BY CASE " +
            "  WHEN c.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "  ELSE 1 " +
            "END, c.name ASC")
    Page<Commerce> findActiveByNameContainingRankedAndLocality(
            @Param("query") String query,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @Query("SELECT (COUNT(c) > 0) FROM commerces c " +
            "WHERE c.deleted = false " +
            "AND c.normalizedName = :normalizedName " +
            "AND c.normalizedAddress = :normalizedAddress " +
            "AND c.normalizedLocality = :normalizedLocality")
    boolean existsActiveByNormalizedIdentity(
            @Param("normalizedName") String normalizedName,
            @Param("normalizedAddress") String normalizedAddress,
            @Param("normalizedLocality") String normalizedLocality
    );

    @Query("SELECT (COUNT(c) > 0) FROM commerces c " +
            "WHERE c.deleted = false " +
            "AND c.commerceId <> :commerceId " +
            "AND c.normalizedName = :normalizedName " +
            "AND c.normalizedAddress = :normalizedAddress " +
            "AND c.normalizedLocality = :normalizedLocality")
    boolean existsActiveByNormalizedIdentityExcludingId(
            @Param("commerceId") UUID commerceId,
            @Param("normalizedName") String normalizedName,
            @Param("normalizedAddress") String normalizedAddress,
            @Param("normalizedLocality") String normalizedLocality
    );

    @Query("SELECT DISTINCT c FROM commerces c " +
            "JOIN c.commerceTypes ct " +
            "WHERE c.deleted = false " +
            "AND ct.name IN :commerceTypes " +
            "AND c.normalizedLocality = :normalizedLocality")
    Page<Commerce> findActiveByCommerceTypesAndLocality(
            @Param("commerceTypes") Set<CommerceTypeEnum> commerceTypes,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM commerces c " +
            "JOIN c.commerceTypes ct " +
            "WHERE c.deleted = false " +
            "AND ct.name IN :commerceTypes " +
            "AND c.commerceId NOT IN :excludeIds " +
            "AND c.normalizedLocality = :normalizedLocality")
    Page<Commerce> findActiveByCommerceTypesExcludingIdsAndLocality(
            @Param("commerceTypes") Set<CommerceTypeEnum> commerceTypes,
            @Param("excludeIds") Set<UUID> excludeIds,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    // Nuevo método para buscar comercio por userId (no borrado)
    @Query("SELECT c FROM commerces c WHERE c.user.userId = :userId AND c.deleted = false")
    Optional<Commerce> findByUserId(@Param("userId") UUID userId);
}