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

    public PaymentStatusResponse handlePaymentSuccess(UUID orderId) {
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
