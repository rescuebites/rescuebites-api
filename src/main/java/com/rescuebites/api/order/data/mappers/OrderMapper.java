package com.rescuebites.api.order.data.mappers;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForClientResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForCommerceResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import com.rescuebites.api.order.data.models.Order;

import java.math.BigDecimal;
import java.util.List;
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
                commerce.getCommerceTypes().stream()
                        .map(CommerceType::getName)
                        .collect(Collectors.toList()),
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
                order.getNotes(),
                order.getCancellationReason()
        );
    }

    /**
     * Mapea Order a OrderSummaryForClientResponse (listado para el cliente).
     * Incluye información básica del pedido y datos del comercio.
     */
    public static OrderSummaryForClientResponse toOrderSummaryForClient(Order order) {
        Commerce commerce = order.getCommerce();

        return new OrderSummaryForClientResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                commerce.getCommerceId(),
                commerce.getName(),
                commerce.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                order.getItems().size(),
                order.getCreatedAt(),
                order.getTotal(),
                order.getStatus()
        );
    }

    /**
     * Mapea Order a OrderSummaryForCommerceResponse (listado para el comercio).
     * Incluye información básica del pedido y datos del cliente.
     */
    public static OrderSummaryForCommerceResponse toOrderSummaryForCommerce(Order order) {
        Client client = order.getClient();

        List<ImageResponse> clientImages = client.getImage() != null
                ? List.of(ImageMapper.toImageResponse(client.getImage()))
                : List.of();

        return new OrderSummaryForCommerceResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                client.getClientId(),
                client.getFirstName(),
                client.getLastName(),
                clientImages,
                order.getItems().size(),
                order.getCreatedAt(),
                order.getTotal(),
                order.getStatus()
        );
    }
}