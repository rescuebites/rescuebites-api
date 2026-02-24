package com.rescuebites.api.order.facades.implementations;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.cart.repositories.ICartRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.facades.interfaces.IOrderValidationFacade;
import com.rescuebites.api.order.repositories.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderValidationFacade implements IOrderValidationFacade {

    private final ICartRepository cartRepository;
    private final IOrderRepository orderRepository;

    @Override
    public Cart findCartByClientId(UUID clientId) {
        return cartRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "clientId", clientId));
    }

    @Override
    public Order findOrderByIdAndClientId(UUID orderId, UUID clientId) {
        return orderRepository.findByIdAndClientId(orderId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    public Order findOrderByIdAndCommerceId(UUID orderId, UUID commerceId) {
        return orderRepository.findByIdAndCommerceId(orderId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    public void validateCartNotEmpty(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new ValidationException("El carrito está vacío. Agrega productos antes de crear un pedido");
        }
    }

    @Override
    public void validateAllItemsFromSameCommerce(Cart cart, UUID commerceId) {
        boolean allFromSameCommerce = cart.getItems().stream()
                .allMatch(item -> item.getProduct().getCommerce().getCommerceId().equals(commerceId));

        if (!allFromSameCommerce) {
            throw new ValidationException(
                    "Todos los productos en el carrito deben ser del mismo comercio. " +
                            "Por favor, crea pedidos separados para cada comercio"
            );
        }
    }

    @Override
    public void validateStockForAllItems(Cart cart) {
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new ValidationException(
                        String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                                item.getProduct().getName(),
                                item.getProduct().getStock(),
                                item.getQuantity())
                );
            }
        }
    }

    @Override
    public String generateOrderNumber() {
        // Formato: ORD-YYYYMMDD-HHMMSS-RANDOM
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);

        return String.format("ORD-%s-%s-%d", datePart, timePart, randomPart);
    }
}