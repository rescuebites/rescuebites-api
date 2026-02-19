package com.rescuebites.api.order.controllers.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID orderItemId,
        UUID productId,
        String productName,
        BigDecimal originalPrice,
        BigDecimal discountPercentage,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) {}