package com.rescuebites.api.order.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderItemResponse(
        UUID orderItemId,
        UUID productId,
        String productName,
        String productDescription,
        BigDecimal originalPrice,
        BigDecimal discountPercentage,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        List<ImageResponse> images
) {}