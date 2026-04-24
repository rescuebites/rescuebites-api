package com.rescuebites.api.cart.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CommerceCartSummary(
        UUID commerceId,
        String commerceName,
        String address,
        String commerceLocality,
        List<CommerceTypeEnum> commerceTypes,
        List<ImageResponse> commerceImages,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        Integer itemCount
) {}