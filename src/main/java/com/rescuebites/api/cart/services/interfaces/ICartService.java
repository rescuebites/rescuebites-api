package com.rescuebites.api.cart.services.interfaces;

import com.rescuebites.api.cart.controllers.requests.AddToCartRequest;
import com.rescuebites.api.cart.controllers.requests.UpdateCartItemRequest;
import com.rescuebites.api.cart.controllers.requests.UpdatePaymentMethodRequest;
import com.rescuebites.api.cart.controllers.responses.CartResponse;

import java.util.UUID;

public interface ICartService {

    CartResponse getCart(UUID clientId);

    CartResponse addToCart(UUID clientId, AddToCartRequest request);

    CartResponse updateCartItem(UUID clientId, UUID cartItemId, UpdateCartItemRequest request);

    void removeFromCart(UUID clientId, UUID cartItemId);

    void clearCart(UUID clientId);

    CartResponse updatePaymentMethod(UUID clientId, UpdatePaymentMethodRequest request);
}