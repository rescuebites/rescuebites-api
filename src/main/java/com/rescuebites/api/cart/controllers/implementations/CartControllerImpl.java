package com.rescuebites.api.cart.controllers.implementations;

import com.rescuebites.api.cart.controllers.interfaces.ICartController;
import com.rescuebites.api.cart.controllers.requests.AddToCartRequest;
import com.rescuebites.api.cart.controllers.requests.UpdateCartItemRequest;
import com.rescuebites.api.cart.controllers.requests.UpdatePaymentMethodRequest;
import com.rescuebites.api.cart.controllers.responses.CartResponse;
import com.rescuebites.api.cart.services.interfaces.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CartControllerImpl implements ICartController {

    private final ICartService cartService;

    @Override
    public CartResponse getCart(UUID clientId) {
        return cartService.getCart(clientId);
    }

    @Override
    public CartResponse addToCart(UUID clientId, AddToCartRequest request) {
        return cartService.addToCart(clientId, request);
    }

    @Override
    public CartResponse updateCartItem(UUID clientId, UUID cartItemId, UpdateCartItemRequest request) {
        return cartService.updateCartItem(clientId, cartItemId, request);
    }

    @Override
    public void removeFromCart(UUID clientId, UUID cartItemId) {
        cartService.removeFromCart(clientId, cartItemId);
    }

    @Override
    public void clearCart(UUID clientId) {
        cartService.clearCart(clientId);
    }

    @Override
    public CartResponse updatePaymentMethod(UUID clientId, UpdatePaymentMethodRequest request) {
        return cartService.updatePaymentMethod(clientId, request);
    }
}