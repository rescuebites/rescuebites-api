package com.rescuebites.api.order.data.mappers;

import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.order.controllers.responses.OrderItemResponse;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.data.models.OrderItem;
import com.rescuebites.api.product.data.models.Product;

public class OrderItemMapper {
    public static OrderItem toOrderItem (
            Order order,
            Product product,
            CartItem cartItem
    ){
        return OrderItem.builder()
                .order(order)
                .product(product)
                .productName(product.getName())
                .originalPrice(product.getOriginalPrice())
                .discountPercentage(product.getDiscountPercentage())
                .unitPrice(product.getDiscountedPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getOrderItemId(),
                orderItem.getProduct().getProductId(),
                orderItem.getProductName(),
                orderItem.getOriginalPrice(),
                orderItem.getDiscountPercentage(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
    }
}
