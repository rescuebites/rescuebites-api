package com.rescuebites.api.payment.controllers.implementations;

import com.rescuebites.api.payment.controllers.interfaces.IPaymentController;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentStatusResponse;
import com.rescuebites.api.payment.services.interfaces.IPaymentService;
import com.rescuebites.api.payment.services.payment.PaymentStatusService;
import com.rescuebites.api.payment.services.payment.PaymentConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements IPaymentController {

    private final IPaymentService paymentService;
    private final PaymentStatusService paymentStatusService;
    private final PaymentConfirmationService paymentConfirmationService;

    @Override
    public PaymentLinkResponse createPaymentPreference(UUID orderId, String frontendBaseUrl) {
        return paymentService.createPaymentPreference(orderId, frontendBaseUrl);
    }

    @Override
    public void handleMercadoPagoWebhook(String notification, String xSignature, String xRequestId) {
        paymentService.processWebhookNotification(notification, xSignature, xRequestId);
    }

    @Override
    public ResponseEntity<Void> paymentSuccess(UUID orderId) {
        PaymentStatusResponse response = paymentStatusService.handlePaymentSuccess(orderId);
        return ResponseEntity.status(302).location(URI.create(response.redirectUrl())).build();
    }

    @Override
    public ResponseEntity<Void> paymentFailure(UUID orderId) {
        PaymentStatusResponse response = paymentStatusService.handlePaymentFailure(orderId);
        return ResponseEntity.status(302).location(URI.create(response.redirectUrl())).build();
    }

    @Override
    public ResponseEntity<Void> paymentPending(UUID orderId) {
        PaymentStatusResponse response = paymentStatusService.handlePaymentPending(orderId);
        return ResponseEntity.status(302).location(URI.create(response.redirectUrl())).build();
    }

    @Override
    public ResponseEntity<Void> confirmOrder(UUID orderId) {
        // Endpoint público invocado por el frontend al aterrizar en la página de éxito.
        // Ejecuta el fallback idempotente para confirmar la orden si aún no fue confirmada.
        paymentConfirmationService.confirmPaymentIfNotAlreadyConfirmed(orderId);
        return ResponseEntity.ok().build();
    }
}