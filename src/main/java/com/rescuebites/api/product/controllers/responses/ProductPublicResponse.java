package com.rescuebites.api.product.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.product.data.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProductPublicResponse(
        UUID productId,
        String name,
        Integer stock,
        BigDecimal originalPrice,
        BigDecimal discountPercentage,
        BigDecimal discountedPrice,
        LocalDate expirationDate,
        List<ImageResponse> images,
        ProductCategory category,
        Set<PreferenceType> preferences
) {}