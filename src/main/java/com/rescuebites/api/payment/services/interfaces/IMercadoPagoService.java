package com.rescuebites.api.payment.services.interfaces;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentWebhookData;

import java.util.Optional;

public interface IMercadoPagoService {

    PaymentLinkResponse createPaymentPreference(Order order);

    Optional<PaymentWebhookData> processWebhookNotification(String notification);
}