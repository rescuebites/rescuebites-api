package com.rescuebites.api.order.services.implementations;

import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.cart.repositories.ICartRepository;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.notifications.Interfaces.INotificationService;
import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import com.rescuebites.api.order.data.mappers.OrderItemMapper;
import com.rescuebites.api.order.data.mappers.OrderMapper;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.data.models.OrderItem;
import com.rescuebites.api.order.facades.interfaces.IOrderValidationFacade;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.order.services.interfaces.IClientOrderService;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.services.interfaces.IWhatsAppService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.cart.utils.CartConstants.SERVICE_FEE;
import static com.rescuebites.api.order.data.enums.OrderStatus.CONFIRMED;
import static com.rescuebites.api.order.data.enums.PaymentMethod.CASH;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements IClientOrderService {

    private final IProductRepository productRepository;
    private final ICartRepository cartRepository;
    private final IClientFacade clientFacade;
    private final ICommerceFacade commerceFacade;
    private final IOrderValidationFacade orderValidationFacade;
    private final IOrderRepository orderRepository;
    private final IWhatsAppService whatsAppService;
    private final INotificationService notificationService;

    @Override
    @Transactional
    @CacheEvict(value = { "activeProducts", "productsByCommerce", "productById",
            "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice" }, allEntries = true)
    public OrderResponse createOrder(UUID clientId, CreateOrderRequest request) {
        log.info("ORDEN NUEVA");
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(request.commerceId());
        Cart cart = orderValidationFacade.findCartByClientId(clientId);

        validateCartForOrder(cart, request.commerceId());

        // Validar disponibilidad horaria del comercio
        orderValidationFacade.validateCommerceAvailability(commerce, request.scheduledPickupTime());

        PaymentMethod paymentMethod = cart.getSelectedPaymentMethod();
        String orderNumber = orderValidationFacade.generateOrderNumber();
        Order order = OrderMapper.toOrder(client, commerce, orderNumber, request, paymentMethod);

        // Guardar horario programado de retiro si fue proporcionado
        if (request.scheduledPickupTime() != null) {
            order.setScheduledPickupTime(request.scheduledPickupTime());
        }

        createOrderItemsAndUpdateStock(order, cart);
        calculateOrderTotals(order);
        notificationService.notifyNewOrderCommerce(order);

        if (CASH.equals(paymentMethod)) {
            order.setStatus(CONFIRMED);
            order.setConfirmedAt(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);
        clearClientCart(cart);

        whatsAppService.notifyCommerceNewOrder(savedOrder);

        return OrderMapper.toOrderResponse(savedOrder);
    }

    private void validateCartForOrder(Cart cart, UUID commerceId) {
        orderValidationFacade.validateCartNotEmpty(cart);
        orderValidationFacade.validateAllItemsFromSameCommerce(cart, commerceId);
        orderValidationFacade.validateStockForAllItems(cart);
    }

    private void createOrderItemsAndUpdateStock(Order order, Cart cart) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = createOrderItemFromCartItem(order, cartItem);
            orderItems.add(orderItem);

            // subtotal = suma de originalPrice × quantity (sin descuento)
            subtotal = subtotal.add(
                    orderItem.getOriginalPrice()
                            .multiply(BigDecimal.valueOf(orderItem.getQuantity())));

            updateProductStock(cartItem.getProduct(), cartItem.getQuantity());
        }

        order.setItems(orderItems);
        order.setSubtotal(subtotal);
    }

    private OrderItem createOrderItemFromCartItem(Order order, CartItem cartItem) {
        Product product = cartItem.getProduct();
        OrderItem orderItem = OrderItemMapper.toOrderItem(order, product, cartItem);
        orderItem.calculateSubtotal();
        return orderItem;
    }

    private void updateProductStock(Product product, Integer quantitySold) {
        product.setStock(product.getStock() - quantitySold);

        if (product.getStock() == 0) {
            product.setActive(false);
            // ACÁ FALTA NOTIFICAR AL COMERCIO QUE SE QUEDÓ SIN STOCK DE ESE PRODUCTO, PARA
            // QUE LO REPONGA SI QUIERE SEGUIR VENDIÉNDOLO
        }

        productRepository.save(product);
    }

    private void calculateOrderTotals(Order order) {
        BigDecimal discountedSubtotal = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(discountedSubtotal.add(SERVICE_FEE));
    }

    private void clearClientCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(UUID clientId, UUID orderId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Order order = orderValidationFacade.findOrderByIdAndClientId(orderId, clientId);

        return OrderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public Page<OrderResponse> getClientOrders(UUID clientId, Pageable pageable) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Page<Order> orders = orderRepository.findByClientId(clientId, pageable);

        return orders.map(OrderMapper::toOrderResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "activeProducts", "productsByCommerce", "productById",
            "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice" }, allEntries = true)
    public void cancelOrder(UUID clientId, UUID orderId, String reason) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        Order order = orderValidationFacade.findOrderByIdAndClientId(orderId, clientId);

        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != CONFIRMED) {
            throw new ValidationException("Solo puedes cancelar pedidos pendientes o confirmados");
        }

        // Restaurar stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            product.setActive(true);
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);
        orderRepository.save(order);
        notificationService.notifyOrderCanceledCommerce(order);

        whatsAppService.notifyCommerceCancelledOrder(order);
    }
}
