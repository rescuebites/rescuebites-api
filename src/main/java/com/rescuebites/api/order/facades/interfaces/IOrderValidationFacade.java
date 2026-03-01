package com.rescuebites.api.order.facades.interfaces;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.order.data.models.Order;

import java.time.LocalTime;
import java.util.UUID;

public interface IOrderValidationFacade {

    Cart findCartByClientId(UUID clientId);

    Order findOrderByIdAndClientId(UUID orderId, UUID clientId);

    Order findOrderByIdAndCommerceId(UUID orderId, UUID commerceId);

    void validateCartNotEmpty(Cart cart);

    void validateAllItemsFromSameCommerce(Cart cart, UUID commerceId);

    void validateStockForAllItems(Cart cart);

    void validateCommerceAvailability(Commerce commerce, LocalTime scheduledPickupTime);

    String generateOrderNumber();
}