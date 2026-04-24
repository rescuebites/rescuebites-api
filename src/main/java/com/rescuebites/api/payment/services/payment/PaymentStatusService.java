package com.rescuebites.api.payment.services.payment;

import com.rescuebites.api.payment.config.PaymentRedirectConfig;
import com.rescuebites.api.payment.controllers.responses.PaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentRedirectConfig redirectConfig;
    private final PaymentConfirmationService paymentConfirmationService;

    /**
     * Maneja la redirección de éxito de Mercado Pago.
     * Además de redirigir al frontend, intenta confirmar el pedido aquí como
     * fallback para entornos donde el webhook no llega (sandbox con ngrok offline, etc).
     * En producción el webhook es el mecanismo canónico, pero este fallback garantiza
     * que el estado del pedido se actualice cuando el usuario termina el checkout.
     */
    public PaymentStatusResponse handlePaymentSuccess(UUID orderId) {
        try {
            paymentConfirmationService.confirmPaymentIfNotAlreadyConfirmed(orderId);
        } catch (Exception e) {
            // No bloquear la redirección si ya estaba confirmado u ocurre otro error
            log.warn("No se pudo confirmar el pago por redirect-callback para orderId={}: {}", orderId, e.getMessage());
        }
        return buildPaymentResponse(
                orderId,
                "SUCCESS",
                "El pago se ha procesado correctamente",
                redirectConfig::getSuccessUrl
        );
    }

    public PaymentStatusResponse handlePaymentFailure(UUID orderId) {
        return buildPaymentResponse(
                orderId,
                "FAILURE",
                "El pago no se pudo procesar. Por favor, intenta nuevamente",
                redirectConfig::getFailureUrl
        );
    }

    public PaymentStatusResponse handlePaymentPending(UUID orderId) {
        return buildPaymentResponse(
                orderId,
                "PENDING",
                "El pago está pendiente de confirmación",
                redirectConfig::getPendingUrl
        );
    }

    private PaymentStatusResponse buildPaymentResponse(
            UUID orderId,
            String status,
            String message,
            Supplier<String> urlSupplier
    ) {
        String redirectUrl = urlSupplier.get() + "?orderId=" + orderId;
        return new PaymentStatusResponse(orderId, status, message, redirectUrl);
    }
}
