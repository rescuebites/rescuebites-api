package com.rescuebites.api.order.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/* Response para listado de pedidos desde la perspectiva del cliente.  */
public record OrderSummaryForClientResponse(
        UUID orderId,
        String orderNumber,
        UUID commerceId,
        String commerceName,
        List<ImageResponse> commerceImages,
        int totalItems,
        LocalDateTime createdAt,
        BigDecimal total,
        OrderStatus status
) {
}
