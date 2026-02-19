package com.rescuebites.api.cart.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartItemResponse(
        UUID cartItemId,
        UUID productId,
        String productName,
        String description,
        BigDecimal originalPrice,
        BigDecimal discountPercentage,
        BigDecimal unitPrice,
        Integer quantity,
        Integer availableStock,
        BigDecimal subtotal,
        UUID commerceId,
        String commerceName,
        String commerceAddress,
        List<ImageResponse> images
) {}