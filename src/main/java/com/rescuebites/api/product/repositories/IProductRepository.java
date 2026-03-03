package com.rescuebites.api.product.repositories;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.data.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepository extends JpaRepository<Product, UUID> {

    /*
    Queries para el dueño del comercio (ve todos los productos, activos e inactivos)
     */

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.commerce.commerceId = :commerceId")
    Page<Product> findByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.commerce.commerceId = :commerceId")
    Optional<Product> findByIdAndCommerceId(
            @Param("productId") UUID productId,
            @Param("commerceId") UUID commerceId
    );

    /*
     Queries para el home (solo productos activos)
     */

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.active = true")
    Page<Product> findAllActive(Pageable pageable);

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.active = true")
    Optional<Product> findByIdAndActive(@Param("productId") UUID productId);

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.commerce.commerceId = :commerceId AND p.active = true")
    Page<Product> findActiveByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.active = true ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findAllActiveOrderByDiscountedPriceAsc(Pageable pageable);

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.active = true AND p.commerceType = :commerceType " +
            "ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findActiveByCommerceTypeOrderByDiscountedPriceAsc(
            @Param("commerceType") CommerceTypeEnum commerceType,
            Pageable pageable
    );

    /*
    Queries para filtrar productos según las preferencias del cliente
     */

    @Query("SELECT DISTINCT p FROM products p " +
            "WHERE p.active = true " +
            "AND (SELECT COUNT(pref) FROM p.preferenceType pref WHERE pref IN :preferences) > 0 " +
            "ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findActiveProductsWithPreferences(
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );

    /*
     Queries para búsqueda optimizada (barra de búsqueda)
     */

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.active = true " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY CASE " +
            "  WHEN LOWER(p.name) LIKE LOWER(CONCAT(:query, '%')) THEN 0 " +
            "  ELSE 1 " +
            "END, p.name ASC")
    Page<Product> findActiveByNameContaining(
            @Param("query") String query,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p LEFT JOIN products p2 " +
            "ON p.productId = p2.productId AND LOWER(p2.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "WHERE p.active = true " +
            "AND LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "AND p2.productId IS NULL")
    Page<Product> findActiveByDescriptionContainingExcludingName(
            @Param("query") String query,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.active = true " +
            "AND p.commerce.commerceId IN (SELECT c.commerceId FROM commerces c " +
            "WHERE c.deleted = false AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> findActiveByCommerceName(
            @Param("query") String query,
            Pageable pageable
    );

    /*
        Queries para productos filtrados por preferencias del cliente (registrado o logueado)
     */

    @Query("SELECT p FROM products p WHERE p.active = true " +
            "AND p.commerce.commerceId = :commerceId " +
            "AND (SELECT COUNT(pref) FROM p.preferenceType pref WHERE pref IN :preferences) > 0")
    List<Product> findActiveByCommerceWithPreferences(
            @Param("commerceId") UUID commerceId,
    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
            @Param("preferences") List<PreferenceType> preferences
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND p.commerce.commerceId = :commerceId " +
            "AND pref IN :preferences")
    Page<Product> findActiveByCommerceIdWithPreferences(
            @Param("commerceId") UUID commerceId,
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND pref IN :preferences " +
            "ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findActiveProductsWithPreferencesOrderByPrice(
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND p.commerceType = :commerceType " +
            "AND pref IN :preferences " +
            "ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findActiveByCommerceTypeWithPreferencesOrderByPrice(
            @Param("commerceType") CommerceTypeEnum commerceType,
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );
}