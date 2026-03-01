package com.rescuebites.api.cart.facades.implementations;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.facades.interfaces.ICartValidationFacade;
import com.rescuebites.api.cart.repositories.ICartRepository;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.commerce.data.enums.CommerceScheduleStatus;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.utils.BusinessHoursUtils;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.UnauthorizedException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartValidationFacade implements ICartValidationFacade {

    private final IClientRepository clientRepository;
    private final IProductRepository productRepository;
    private final ICartRepository cartRepository;

    @Override
    public Client findClientById(UUID clientId) {
        return clientRepository.findByClientIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

    @Override
    public Product findActiveProductById(UUID productId) {
        return productRepository.findByIdAndActive(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @Override
    public Cart findOrCreateCartByClient(Client client) {
        return cartRepository.findByClientId(client.getClientId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .client(client)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public void validateStockAvailability(Product product, Integer requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new ValidationException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                            product.getStock(), requestedQuantity)
            );
        }
    }

    @Override
    public void validateCartBelongsToClient(Cart cart, UUID clientId) {
        if (!cart.getClient().getClientId().equals(clientId)) {
            throw new UnauthorizedException("Este carrito no pertenece al cliente autenticado");
        }
    }

    @Override
    public void validateCommerceNotClosedForDay(Commerce commerce) {
        CommerceScheduleStatus status = BusinessHoursUtils.getCommerceStatus(
                commerce, LocalDateTime.now());

        if (status.isClosedForDay()) {
            throw new ValidationException(
                    "El comercio '" + commerce.getName() + "' está cerrado por hoy. " +
                            "No es posible agregar productos a tu carrito en este momento");
        }
    }
}