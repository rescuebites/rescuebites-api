package com.rescuebites.api.order.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/* Response para listado de pedidos desde la perspectiva del comercio. */
public record OrderSummaryForCommerceResponse(
        UUID orderId,
        String orderNumber,
        UUID clientId,
        String clientName,
        String clientLastName,
        List<ImageResponse> clientImages,
        int totalItems,
        LocalDateTime createdAt,
        BigDecimal total,
        OrderStatus status
) {
}
