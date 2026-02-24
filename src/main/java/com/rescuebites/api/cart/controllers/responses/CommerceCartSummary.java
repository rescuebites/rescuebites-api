package com.rescuebites.api.cart.controllers.responses;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CommerceCartSummary(
        UUID commerceId,
        String commerceName,
        String address,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        Integer itemCount
) {}