package com.rescuebites.api.payment.controllers.responses;

import java.util.UUID;

public record PaymentWebhookData(
        UUID orderId,
        String paymentId,
        String status
) {}