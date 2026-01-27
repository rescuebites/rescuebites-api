package com.rescuebites.api.product.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProductPublicResponse(
        UUID productId,
        String name,
        Integer stock,
        BigDecimal originalPrice,
        BigDecimal discountPercentage,
        BigDecimal discountedPrice,
        LocalDate expirationDate,
        List<ImageResponse> images
) {}