package com.rescuebites.api.payment.controllers.implementations;

import com.rescuebites.api.payment.controllers.interfaces.IPaymentController;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentStatusResponse;
import com.rescuebites.api.payment.services.interfaces.IPaymentService;
import com.rescuebites.api.payment.services.payment.PaymentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements IPaymentController {

    private final IPaymentService paymentService;
    private final PaymentStatusService paymentStatusService;

    @Override
    public PaymentLinkResponse createPaymentPreference(UUID orderId) {
        return paymentService.createPaymentPreference(orderId);
    }

    @Override
    public void handleMercadoPagoWebhook(String notification, String xSignature, String xRequestId) {
        paymentService.processWebhookNotification(notification, xSignature, xRequestId);
    }

    @Override
    public PaymentStatusResponse paymentSuccess(UUID orderId) {
        return paymentStatusService.handlePaymentSuccess(orderId);
    }

    @Override
    public PaymentStatusResponse paymentFailure(UUID orderId) {
        return paymentStatusService.handlePaymentFailure(orderId);
    }

    @Override
    public PaymentStatusResponse paymentPending(UUID orderId) {
        return paymentStatusService.handlePaymentPending(orderId);
    }
}