package com.rescuebites.api.order.controllers.responses;

import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        String orderNumber,
        UUID clientId,
        String clientName,
        UUID commerceId,
        String commerceName,
        String commerceAddress,
        String commercePhone,
        List<OrderItemResponse> items,
        BigDecimal subtotal,
        BigDecimal serviceFee,
        BigDecimal total,
        OrderStatus status,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        String notes
) {}