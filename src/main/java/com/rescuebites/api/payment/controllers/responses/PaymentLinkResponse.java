package com.rescuebites.api.payment.controllers.responses;

public record PaymentLinkResponse(
        String preferenceId,
        String initPoint,        // URL para producción
        String sandboxInitPoint  // URL para testing
) {}