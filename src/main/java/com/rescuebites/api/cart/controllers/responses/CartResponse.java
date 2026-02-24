package com.rescuebites.api.cart.controllers.responses;

import com.rescuebites.api.order.data.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CartResponse(
        UUID cartId,
        Map<UUID, CommerceCartSummary> commerceSummaries,
        BigDecimal subtotal,
        BigDecimal serviceFee,
        BigDecimal total,
        Integer totalItems,
        PaymentMethod selectedPaymentMethod,
        PaymentMethod lastUsedPaymentMethod
) {}