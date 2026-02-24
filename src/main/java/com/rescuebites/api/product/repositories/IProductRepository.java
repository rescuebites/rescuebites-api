package com.rescuebites.api.product.repositories;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.data.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepository extends JpaRepository<Product, UUID> {

    // Queries para el dueño del comercio (ve todos los productos, activos e inactivos)
    @Query("SELECT p FROM products p WHERE p.commerce.commerceId = :commerceId")
    Page<Product> findByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.commerce.commerceId = :commerceId")
    Optional<Product> findByIdAndCommerceId(
            @Param("productId") UUID productId,
            @Param("commerceId") UUID commerceId
    );

    // Queries para clientes (solo productos activos)
    @Query("SELECT p FROM products p WHERE p.active = true")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM products p WHERE p.productId = :productId AND p.active = true")
    Optional<Product> findByIdAndActive(@Param("productId") UUID productId);

    @Query("SELECT p FROM products p WHERE p.commerce.commerceId = :commerceId AND p.active = true")
    Page<Product> findActiveByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @Query("SELECT p FROM products p WHERE p.active = true ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findAllActiveOrderByDiscountedPriceAsc(Pageable pageable);

    @Query("SELECT p FROM products p " +
            "WHERE p.active = true AND p.commerceType = :commerceType " +
            "ORDER BY (p.originalPrice - (p.originalPrice * p.discountPercentage / 100)) ASC")
    Page<Product> findActiveByCommerceTypeOrderByDiscountedPriceAsc(
            @Param("commerceType") CommerceTypeEnum commerceType,
            Pageable pageable
    );
}