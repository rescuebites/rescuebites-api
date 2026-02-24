package com.rescuebites.api.cart.repositories;

import com.rescuebites.api.cart.data.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, UUID> {

    @Query("""
            SELECT ci\s
            FROM cart_items ci\s
            WHERE ci.cart.cartId = :cartId\s
            AND ci.product.productId = :productId
           \s""")
    Optional<CartItem> findByCartIdAndProductId(
            @Param("cartId") UUID cartId,
            @Param("productId") UUID productId
    );
}