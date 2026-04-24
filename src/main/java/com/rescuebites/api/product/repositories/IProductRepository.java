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

    @EntityGraph(attributePaths = {"preferenceType", "conditions", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.commerce.commerceId = :commerceId")
    Optional<Product> findByIdAndCommerceId(
            @Param("productId") UUID productId,
            @Param("commerceId") UUID commerceId
    );

    @EntityGraph(attributePaths = {"preferenceType", "conditions", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.active = true")
    Optional<Product> findByIdAndActive(@Param("productId") UUID productId);

    /*
        Queries para productos filtrados por preferencias del cliente (registrado o autenticado)
     */

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "ORDER BY p.calculatedDiscountedPrice ASC")
    Page<Product> findActiveProductsWithPreferencesOrderByPriceAndLocality(
            @Param("preferences") List<PreferenceType> preferences,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    /*
     Queries para sugerencias (autocomplete)
     */

    @EntityGraph(attributePaths = {"commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   ELSE 1 " +
            "END, p.name ASC")
    Page<Product> suggestProductsHierarchy(
            @Param("query") String query,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   ELSE 1 " +
            "END, p.name ASC")
    Page<Product> suggestProductsHierarchyWithPreferences(
            @Param("query") String query,
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   ELSE 1 " +
            "END, p.name ASC")
    Page<Product> suggestProductsHierarchyWithPreferencesAndLocality(
            @Param("query") String query,
            @Param("preferences") List<PreferenceType> preferences,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @Query("SELECT (COUNT(p) > 0) FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND LOWER(p.name) = LOWER(:name) " +
            "AND p.category = :category " +
            "AND ((:expirationDate IS NULL AND p.expirationDate IS NULL) OR p.expirationDate = :expirationDate) " +
            "AND p.originalPrice = :originalPrice " +
            "AND p.discountPercentage = :discountPercentage " +
            "AND (" +
            "   (SIZE(p.preferenceType) = 0 AND :preferencesEmpty = true) " +
            "   OR (SIZE(p.preferenceType) = :preferencesSize AND (SELECT COUNT(pref) FROM p.preferenceType pref WHERE pref IN :preferences) = :preferencesSize)" +
            ")")
    boolean existsIdenticalProductInCommerce(
            @Param("commerceId") UUID commerceId,
            @Param("name") String name,
            @Param("category") com.rescuebites.api.product.data.enums.ProductCategory category,
            @Param("expirationDate") java.time.LocalDate expirationDate,
            @Param("originalPrice") java.math.BigDecimal originalPrice,
            @Param("discountPercentage") java.math.BigDecimal discountPercentage,
            @Param("preferences") List<PreferenceType> preferences,
            @Param("preferencesSize") long preferencesSize,
            @Param("preferencesEmpty") boolean preferencesEmpty
    );

    @Query("SELECT (COUNT(p) > 0) FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND p.productId <> :productId " +
            "AND LOWER(p.name) = LOWER(:name) " +
            "AND p.category = :category " +
            "AND ((:expirationDate IS NULL AND p.expirationDate IS NULL) OR p.expirationDate = :expirationDate) " +
            "AND p.originalPrice = :originalPrice " +
            "AND p.discountPercentage = :discountPercentage " +
            "AND (" +
            "   (SIZE(p.preferenceType) = 0 AND :preferencesEmpty = true) " +
            "   OR (SIZE(p.preferenceType) = :preferencesSize AND (SELECT COUNT(pref) FROM p.preferenceType pref WHERE pref IN :preferences) = :preferencesSize)" +
            ")")
    boolean existsIdenticalProductInCommerceExcludingId(
            @Param("commerceId") UUID commerceId,
            @Param("productId") UUID productId,
            @Param("name") String name,
            @Param("category") com.rescuebites.api.product.data.enums.ProductCategory category,
            @Param("expirationDate") java.time.LocalDate expirationDate,
            @Param("originalPrice") java.math.BigDecimal originalPrice,
            @Param("discountPercentage") java.math.BigDecimal discountPercentage,
            @Param("preferences") List<PreferenceType> preferences,
            @Param("preferencesSize") long preferencesSize,
            @Param("preferencesEmpty") boolean preferencesEmpty
    );

    /*
     * Paginación segura: primero traer IDs (sin fetch de colecciones), luego cargar detalles por IN (:ids).
     */

    @Query("SELECT p.productId FROM products p WHERE p.active = true AND p.commerce.normalizedLocality = :normalizedLocality")
    Page<UUID> findAllActiveIdsByLocality(@Param("normalizedLocality") String normalizedLocality, Pageable pageable);

    @Query("SELECT p.productId FROM products p WHERE p.active = true AND p.commerce.commerceId = :commerceId")
    Page<UUID> findActiveIdsByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @Query("SELECT p.productId FROM products p WHERE p.active = true AND p.commerce.commerceId = :commerceId ORDER BY p.stock ASC")
    Page<UUID> findActiveIdsByCommerceIdOrderByStockAsc(@Param("commerceId") UUID commerceId, Pageable pageable);

    @Query("SELECT p.productId FROM products p " +
            "WHERE p.active = true " +
            "AND p.commerce.commerceId = :commerceId " +
            "AND p.expirationDate IS NOT NULL " +
            "AND p.expirationDate BETWEEN :today AND :limitDate " +
            "ORDER BY p.expirationDate ASC")
    Page<UUID> findExpiringIdsByCommerceId(
            @Param("commerceId") UUID commerceId,
            @Param("today") java.time.LocalDate today,
            @Param("limitDate") java.time.LocalDate limitDate,
            Pageable pageable
    );

    @Query("SELECT p.productId FROM products p WHERE p.active = true AND p.commerce.normalizedLocality = :normalizedLocality ORDER BY p.calculatedDiscountedPrice ASC")
    Page<UUID> findAllActiveIdsOrderByDiscountedPriceAscAndLocality(@Param("normalizedLocality") String normalizedLocality, Pageable pageable);

    @Query("SELECT p.productId FROM products p WHERE p.active = true AND p.commerceType = :commerceType AND p.commerce.normalizedLocality = :normalizedLocality ORDER BY p.calculatedDiscountedPrice ASC")
    Page<UUID> findActiveIdsByCommerceTypeAndLocalityOrderByDiscountedPriceAsc(
            @Param("commerceType") CommerceTypeEnum commerceType,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "conditions", "images", "commerce", "commerce.businessHours"})
    @Query("SELECT DISTINCT p FROM products p WHERE p.productId IN :ids")
    List<Product> findProductsWithDetailsByIds(@Param("ids") List<UUID> ids);

    @EntityGraph(attributePaths = {"preferenceType", "conditions", "images", "commerce", "commerce.businessHours"})
    @Query("SELECT p FROM products p WHERE p.productId = :productId")
    Optional<Product> findByIdWithDetails(@Param("productId") UUID productId);

    @Query("SELECT p.productId FROM products p WHERE p.commerce.commerceId = :commerceId")
    Page<UUID> findIdsByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    // ========== Queries para filtros de vencimiento (gestión del comercio) ==========

    // ALL: productos vencidos + próximos a vencer (≤7 días), ordenados por vencimiento asc
    @Query("SELECT p.productId FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND p.expirationDate IS NOT NULL " +
            "AND p.expirationDate <= :limitDate " +
            "ORDER BY p.expirationDate ASC")
    Page<UUID> findAllWithExpirationIdsByCommerceId(
            @Param("commerceId") UUID commerceId,
            @Param("limitDate") java.time.LocalDate limitDate,
            Pageable pageable
    );

    // CRITICAL: vencen dentro de los próximos 2 días
    @Query("SELECT p.productId FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND p.expirationDate IS NOT NULL " +
            "AND p.expirationDate BETWEEN :today AND :limitDate " +
            "ORDER BY p.expirationDate ASC")
    Page<UUID> findCriticalExpiringIdsByCommerceId(
            @Param("commerceId") UUID commerceId,
            @Param("today") java.time.LocalDate today,
            @Param("limitDate") java.time.LocalDate limitDate,
            Pageable pageable
    );

    // EXPIRED: productos ya vencidos
    @Query("SELECT p.productId FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND p.expirationDate IS NOT NULL " +
            "AND p.expirationDate < :today " +
            "ORDER BY p.expirationDate ASC")
    Page<UUID> findExpiredIdsByCommerceId(
            @Param("commerceId") UUID commerceId,
            @Param("today") java.time.LocalDate today,
            Pageable pageable
    );

    /*
     Queries para búsqueda de productos (sin prioridad por comercio)
     - Coincidencia NO exacta: contiene en nombre o descripción
     - Jerarquía: nombre > descripción
     */

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   WHEN p.normalizedName LIKE CONCAT('%', :query, '%') THEN 1 " +
            "   ELSE 2 " +
            "END, p.name ASC")
    Page<Product> searchProductsByQueryOrdered(
            @Param("query") String query,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   WHEN p.normalizedName LIKE CONCAT('%', :query, '%') THEN 1 " +
            "   ELSE 2 " +
            "END, p.name ASC")
    Page<Product> searchProductsByQueryOrderedWithPreferences(
            @Param("query") String query,
            @Param("preferences") List<PreferenceType> preferences,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "ORDER BY p.calculatedDiscountedPrice ASC")
    Page<Product> findActiveProductsWithPreferencesAndLocality(
            @Param("preferences") List<PreferenceType> preferences,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND p.commerceType = :commerceType " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences) " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "ORDER BY p.calculatedDiscountedPrice ASC")
    Page<Product> findActiveByCommerceTypeWithPreferencesOrderByPriceAndLocality(
            @Param("commerceType") CommerceTypeEnum commerceType,
            @Param("preferences") List<PreferenceType> preferences,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT DISTINCT p FROM products p LEFT JOIN p.preferenceType pref " +
            "WHERE p.active = true " +
            "AND p.commerce.commerceId = :commerceId " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "AND (p.preferenceType IS EMPTY OR pref IN :preferences)")
    Page<Product> findActiveByCommerceIdWithPreferencesAndLocality(
            @Param("commerceId") UUID commerceId,
            @Param("preferences") List<PreferenceType> preferences,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"preferenceType", "images", "commerce"})
    @Query("SELECT p FROM products p WHERE p.active = true AND p.commerce.commerceId = :commerceId " +
            "AND p.commerce.normalizedLocality = :normalizedLocality")
    Page<Product> findActiveByCommerceIdAndLocality(
            @Param("commerceId") UUID commerceId,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.active = true " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "AND p.commerce.normalizedLocality = :normalizedLocality " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   ELSE 1 " +
            "END, p.name ASC")
    Page<Product> suggestProductsHierarchyAndLocality(
            @Param("query") String query,
            @Param("normalizedLocality") String normalizedLocality,
            Pageable pageable
    );

    /*
     * Queries para búsqueda de productos de un comercio específico (buscador del home del comercio)
     * Jerarquía: nombre > descripción
     */

    // Búsqueda con jerarquía estricta:
    // 0 → nombre empieza con la query
    // 1 → nombre contiene la query
    // 2 → descripción empieza con la query
    // 3 → descripción contiene la query
    @EntityGraph(attributePaths = {"preferenceType", "conditions", "images", "commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   WHEN p.normalizedName LIKE CONCAT('%', :query, '%') THEN 1 " +
            "   WHEN p.normalizedDescription LIKE CONCAT(:query, '%') THEN 2 " +
            "   ELSE 3 " +
            "END, p.name ASC")
    Page<Product> searchProductsByCommerceIdAndQuery(
            @Param("commerceId") UUID commerceId,
            @Param("query") String query,
            Pageable pageable
    );

    // Sugerencias (liviano, solo commerce eager): misma jerarquía
    @EntityGraph(attributePaths = {"commerce"})
    @Query("SELECT p FROM products p " +
            "WHERE p.commerce.commerceId = :commerceId " +
            "AND (" +
            "   p.normalizedName LIKE CONCAT('%', :query, '%') " +
            "   OR p.normalizedDescription LIKE CONCAT('%', :query, '%')" +
            ") " +
            "ORDER BY CASE " +
            "   WHEN p.normalizedName LIKE CONCAT(:query, '%') THEN 0 " +
            "   WHEN p.normalizedName LIKE CONCAT('%', :query, '%') THEN 1 " +
            "   WHEN p.normalizedDescription LIKE CONCAT(:query, '%') THEN 2 " +
            "   ELSE 3 " +
            "END, p.name ASC")
    Page<Product> suggestProductsByCommerceIdAndQuery(
            @Param("commerceId") UUID commerceId,
            @Param("query") String query,
            Pageable pageable
    );

    // Queries para el scheduler de vencimiento
    @EntityGraph(attributePaths = {"commerce", "commerce.user"})
    @Query("SELECT p FROM products p " +
            "WHERE p.active = true " +
            "AND p.expirationDate IS NOT NULL " +
            "AND p.expirationDate < :today")
    List<Product> findAllExpiredActiveProducts(@Param("today") java.time.LocalDate today);
}
