package com.rescuebites.api.payment.services.mercadopago;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.payment.controllers.responses.PaymentWebhookData;
import com.rescuebites.api.exceptions.custom_exceptions.IgnorableWebhookException;
import com.rescuebites.api.payment.utils.MercadoPagoConfigUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.rescuebites.api.payment.utils.PaymentConstants.PAYMENT_TYPE;

@Service
@RequiredArgsConstructor
public class WebhookProcessor {

    private final ObjectMapper objectMapper;
    private final IOrderRepository orderRepository;

    public Optional<PaymentWebhookData> processWebhookNotification(String notificationBody) {
        JsonNode notification = parseWebhookNotification(notificationBody);
        validateNotificationType(notification);

        Long paymentId = extractPaymentId(notification);
        return processPaymentNotification(paymentId);
    }

    private JsonNode parseWebhookNotification(String notificationBody) {
        try {
            return objectMapper.readTree(notificationBody);
        } catch (Exception e) {
            throw new IllegalArgumentException("El cuerpo del webhook no es JSON válido", e);
        }
    }

    private void validateNotificationType(JsonNode notification) {
        JsonNode typeNode = notification.get("type");

        // Si no hay type, intentar obtener de data.type (formato alternativo)
        if (typeNode == null || typeNode.isNull()) {
            JsonNode dataNode = notification.get("data");
            if (dataNode != null && !dataNode.isNull()) {
                typeNode = dataNode.get("type");
            }
        }

        // Si aún no hay type, es un webhook de verificación - ignorar
        if (typeNode == null || typeNode.isNull()) {
            throw new IgnorableWebhookException("Webhook sin campo 'type' - ignorado");
        }

        String type = typeNode.asText();
        if (!PAYMENT_TYPE.equals(type)) {
            throw new IgnorableWebhookException("Tipo de notificación no soportado: " + type);
        }
    }

    private Long extractPaymentId(JsonNode notification) {
        JsonNode dataNode = notification.get("data");
        if (dataNode == null || dataNode.get("id") == null) {
            throw new IllegalArgumentException("El webhook no contiene ID de pago válido");
        }
        return dataNode.get("id").asLong();
    }

    private Optional<PaymentWebhookData> processPaymentNotification(Long paymentId) {
        try {
            Payment payment = new PaymentClient().get(paymentId);
            return extractPaymentWebhookData(payment, paymentId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al obtener datos del pago " + paymentId + ": " + e.getMessage(), e);
        }
    }

    private Optional<PaymentWebhookData> extractPaymentWebhookData(Payment payment, Long paymentId) {
        String externalReference = payment.getExternalReference();

        if (externalReference == null) {
            throw new IllegalArgumentException("El pago " + paymentId + " no tiene external reference");
        }

        try {
            UUID orderId = UUID.fromString(externalReference);
            configureCommerceTokenForOrder(orderId);
            return Optional.of(new PaymentWebhookData(orderId, String.valueOf(paymentId), payment.getStatus()));

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UUID inválido en external reference: " + externalReference, e);
        }
    }

    private void configureCommerceTokenForOrder(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            String token = order.getCommerce().getMercadoPagoAccessToken();
            MercadoPagoConfigUtil.configureCommerceToken(token);
        });
    }
}
