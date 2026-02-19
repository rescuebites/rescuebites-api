package com.rescuebites.api.cart.controllers.requests;

import com.rescuebites.api.order.data.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentMethodRequest(

        @NotNull(message = "El método de pago es obligatorio")
        @Schema(description = "Método de pago: MERCADO_PAGO o CASH", example = "MERCADO_PAGO")
        PaymentMethod paymentMethod
) {}
