package com.rescuebites.api.cart.facades.interfaces;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.product.data.models.Product;

import java.util.UUID;

public interface ICartValidationFacade {

    Client findClientById(UUID clientId);

    Product findActiveProductById(UUID productId);

    Cart findOrCreateCartByClient(Client client);

    void validateStockAvailability(Product product, Integer requestedQuantity);

    void validateCartBelongsToClient(Cart cart, UUID clientId);
}