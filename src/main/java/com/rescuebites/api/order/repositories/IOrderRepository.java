package com.rescuebites.api.order.repositories;

import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM orders o WHERE o.client.clientId = :clientId ORDER BY o.createdAt DESC")
    Page<Order> findByClientId(@Param("clientId") UUID clientId, Pageable pageable);

    @Query("SELECT o FROM orders o WHERE o.commerce.commerceId = :commerceId ORDER BY o.createdAt DESC")
    Page<Order> findByCommerceId(@Param("commerceId") UUID commerceId, Pageable pageable);

    @Query("SELECT o FROM orders o WHERE o.orderId = :orderId AND o.client.clientId = :clientId")
    Optional<Order> findByIdAndClientId(
            @Param("orderId") UUID orderId,
            @Param("clientId") UUID clientId
    );

    @Query("SELECT o FROM orders o WHERE o.orderId = :orderId AND o.commerce.commerceId = :commerceId")
    Optional<Order> findByIdAndCommerceId(
            @Param("orderId") UUID orderId,
            @Param("commerceId") UUID commerceId
    );

    @Query("SELECT o FROM orders o WHERE o.commerce.commerceId = :commerceId AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByCommerceIdAndStatus(
            @Param("commerceId") UUID commerceId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );
}