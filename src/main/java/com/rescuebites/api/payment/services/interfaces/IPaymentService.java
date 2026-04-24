package com.rescuebites.api.payment.services.interfaces;

import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;

import java.util.UUID;

public interface IPaymentService {

    PaymentLinkResponse createPaymentPreference(UUID orderId, String frontendBaseUrl);

    void processWebhookNotification(String notification, String xSignature, String xRequestId);
}