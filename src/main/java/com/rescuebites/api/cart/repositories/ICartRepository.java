package com.rescuebites.api.cart.repositories;

import com.rescuebites.api.cart.data.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICartRepository extends JpaRepository<Cart, UUID> {

    @Query("""
            SELECT c\s
            FROM carts c\s
            WHERE c.client.clientId = :clientId
           \s""")
    Optional<Cart> findByClientId(@Param("clientId") UUID clientId);
}