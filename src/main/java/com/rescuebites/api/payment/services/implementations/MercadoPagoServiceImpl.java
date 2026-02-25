package com.rescuebites.api.payment.services.implementations;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentWebhookData;
import com.rescuebites.api.payment.services.interfaces.IMercadoPagoService;
import com.rescuebites.api.payment.services.mercadopago.PreferenceCreator;
import com.rescuebites.api.payment.services.mercadopago.WebhookProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MercadoPagoServiceImpl implements IMercadoPagoService {

    private final PreferenceCreator preferenceCreator;
    private final WebhookProcessor webhookProcessor;

    @Override
    public PaymentLinkResponse createPaymentPreference(Order order) {
        return preferenceCreator.createPaymentPreference(order);
    }

    @Override
    public Optional<PaymentWebhookData> processWebhookNotification(String notificationBody, String xSignature, String xRequestId) {
        return webhookProcessor.processWebhookNotification(notificationBody, xSignature, xRequestId);
    }
}


