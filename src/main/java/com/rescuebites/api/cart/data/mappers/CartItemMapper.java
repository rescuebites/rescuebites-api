package com.rescuebites.api.cart.data.mappers;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.product.data.models.Product;

public class CartItemMapper {

    public static CartItem toCartItem(Cart cart, Product product, Integer quantity) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }
}
