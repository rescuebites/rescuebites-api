package com.rescuebites.api.cart.services.implementations;

import com.rescuebites.api.cart.controllers.requests.AddToCartRequest;
import com.rescuebites.api.cart.controllers.requests.UpdateCartItemRequest;
import com.rescuebites.api.cart.controllers.requests.UpdatePaymentMethodRequest;
import com.rescuebites.api.cart.controllers.responses.CartResponse;
import com.rescuebites.api.cart.data.mappers.CartItemMapper;
import com.rescuebites.api.cart.data.mappers.CartMapper;
import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.cart.facades.interfaces.ICartValidationFacade;
import com.rescuebites.api.cart.repositories.ICartItemRepository;
import com.rescuebites.api.cart.repositories.ICartRepository;
import com.rescuebites.api.cart.services.interfaces.ICartService;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.security.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ICartValidationFacade cartValidationFacade;
    private final IOrderRepository orderRepository;

    private PaymentMethod getLastUsedPaymentMethod(UUID clientId) {
        return orderRepository.findByClientId(clientId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(Order::getPaymentMethod)
                .orElse(null);
    }

    @Override
    @Transactional
    public CartResponse getCart(UUID clientId) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Cart cart = cartValidationFacade.findOrCreateCartByClient(client);

        PaymentMethod lastUsedPaymentMethod = getLastUsedPaymentMethod(clientId);
        return CartMapper.toCartResponse(cart, cart.getSelectedPaymentMethod(), lastUsedPaymentMethod);
    }

    @Override
    @Transactional
    public CartResponse addToCart(UUID clientId, AddToCartRequest request) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Product product = cartValidationFacade.findActiveProductById(request.productId());
        cartValidationFacade.validateStockAvailability(product, request.quantity());
        Cart cart = cartValidationFacade.findOrCreateCartByClient(client);

        cartItemRepository
                .findByCartIdAndProductId(cart.getCartId(), product.getProductId())
                .ifPresentOrElse(
                        existingItem -> updateExistingCartItem(existingItem, product, request.quantity()),
                        () -> createNewCartItem(cart, product, request.quantity())
                );

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        PaymentMethod lastUsedPaymentMethod = getLastUsedPaymentMethod(clientId);
        return CartMapper.toCartResponse(cart, cart.getSelectedPaymentMethod(), lastUsedPaymentMethod);
    }

    private void updateExistingCartItem(CartItem existingItem, Product product, int requestQuantity) {
        int newQuantity = existingItem.getQuantity() + requestQuantity;
        cartValidationFacade.validateStockAvailability(product, newQuantity);

        existingItem.setQuantity(newQuantity);
        existingItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(existingItem);
    }

    private void createNewCartItem(Cart cart, Product product, int quantity) {
        CartItem newItem = CartItemMapper.toCartItem(cart, product, quantity);
        cart.getItems().add(newItem);
        cartItemRepository.save(newItem);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(UUID clientId, UUID cartItemId, UpdateCartItemRequest request) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cartValidationFacade.validateCartBelongsToClient(cartItem.getCart(), clientId);
        cartValidationFacade.validateStockAvailability(cartItem.getProduct(), request.quantity());

        cartItem.setQuantity(request.quantity());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        PaymentMethod lastUsedPaymentMethod = getLastUsedPaymentMethod(clientId);
        return CartMapper.toCartResponse(cart, cart.getSelectedPaymentMethod(), lastUsedPaymentMethod);
    }

    @Override
    @Transactional
    public void removeFromCart(UUID clientId, UUID cartItemId) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cartValidationFacade.validateCartBelongsToClient(cartItem.getCart(), clientId);

        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(UUID clientId) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Cart cart = cartValidationFacade.findOrCreateCartByClient(client);

        cart.clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse updatePaymentMethod(UUID clientId, UpdatePaymentMethodRequest request) {
        Client client = cartValidationFacade.findClientById(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Cart cart = cartValidationFacade.findOrCreateCartByClient(client);

        // Actualizar método de pago
        cart.setSelectedPaymentMethod(request.paymentMethod());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        PaymentMethod lastUsedPaymentMethod = getLastUsedPaymentMethod(clientId);
        return CartMapper.toCartResponse(cart, request.paymentMethod(), lastUsedPaymentMethod);
    }
}