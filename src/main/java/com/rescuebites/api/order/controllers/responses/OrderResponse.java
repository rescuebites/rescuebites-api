package com.rescuebites.api.order.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        String orderNumber,
        UUID commerceId,
        String commerceName,
        String commerceAddress,
        String commerceLocality,
        String commercePhone,
        CommerceTypeEnum commerceType,
        List<ImageResponse> commerceImages,
        List<OrderItemResponse> items,
        BigDecimal subtotal,
        BigDecimal discountedSubtotal,
        BigDecimal serviceFee,
        BigDecimal total,
        OrderStatus status,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        LocalTime scheduledPickupTime,
        String notes
) {}