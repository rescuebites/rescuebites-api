package com.rescuebites.api.order.data.mappers;

import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import com.rescuebites.api.order.data.models.Order;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rescuebites.api.cart.utils.CartConstants.SERVICE_FEE;

public class OrderMapper {

    public static Order toOrder(
            Client client,
            Commerce commerce,
            String orderNumber,
            CreateOrderRequest request,
            PaymentMethod paymentMethod
    ){
        return Order.builder()
                .orderId(UUID.randomUUID())
                .client(client)
                .commerce(commerce)
                .orderNumber(orderNumber)
                .paymentMethod(paymentMethod)
                .notes(request.notes())
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        Commerce commerce = order.getCommerce();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                commerce.getCommerceId(),
                commerce.getName(),
                commerce.getAddress(),
                commerce.getLocality() != null ? commerce.getLocality().getName() : null,
                commerce.getPhone(),
                commerce.getCommerceTypes().isEmpty() ? null
                        : commerce.getCommerceTypes().get(0).getName(),
                commerce.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                order.getItems().stream()
                        .map(OrderItemMapper::toOrderItemResponse)
                        .collect(Collectors.toList()),
                order.calculateSubtotal(),
                order.calculateDiscountedSubtotal(),
                SERVICE_FEE,
                order.getTotal(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                order.getScheduledPickupTime(),
                order.getNotes()
        );
    }
}