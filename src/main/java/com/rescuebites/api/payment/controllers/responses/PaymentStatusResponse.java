package com.rescuebites.api.payment.controllers.responses;

import java.util.UUID;

public record PaymentStatusResponse(
        UUID orderId,
        String status,
        String message,
        String redirectUrl
) {}
