package com.rescuebites.api.order.controllers.requests;

import com.rescuebites.api.order.data.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest (

    @NotNull(message = "El nuevo estado es obligatorio")
    OrderStatus newStatus,

    String reason
) {}