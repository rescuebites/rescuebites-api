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
    private final WebhookSignatureValidator signatureValidator;

    public Optional<PaymentWebhookData> processWebhookNotification(
            String notificationBody, String xSignature, String xRequestId) {

        JsonNode notification = parseWebhookNotification(notificationBody);
        validateNotificationType(notification);

        String dataId = extractDataId(notification);

        // Validar firma HMAC antes de procesar
        signatureValidator.validateSignature(xSignature, xRequestId, dataId);

        Long paymentId = parsePaymentId(dataId);
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

        if (typeNode == null || typeNode.isNull()) {
            JsonNode dataNode = notification.get("data");
            if (dataNode != null && !dataNode.isNull()) {
                typeNode = dataNode.get("type");
            }
        }

        if (typeNode == null || typeNode.isNull()) {
            throw new IgnorableWebhookException("Webhook sin campo 'type' - ignorado");
        }

        String type = typeNode.asText();
        if (!PAYMENT_TYPE.equals(type)) {
            throw new IgnorableWebhookException("Tipo de notificación no soportado: " + type);
        }
    }

    private String extractDataId(JsonNode notification) {
        JsonNode dataNode = notification.get("data");
        if (dataNode == null || dataNode.get("id") == null || dataNode.get("id").isNull()) {
            throw new IgnorableWebhookException("El webhook no contiene ID de pago válido");
        }

        String dataId = dataNode.get("id").asText();
        if (dataId.isBlank()) {
            throw new IgnorableWebhookException("El ID de pago del webhook está vacío");
        }

        return dataId;
    }

    private Long parsePaymentId(String dataId) {
        try {
            return Long.valueOf(dataId);
        } catch (NumberFormatException e) {
            throw new IgnorableWebhookException(
                    "El ID de pago del webhook no es un número válido: " + dataId);
        }
    }

    private Optional<PaymentWebhookData> processPaymentNotification(Long paymentId) {
        Payment payment;

        // Sincronizar configuración del token + llamada al SDK para evitar race conditions
        // MercadoPagoConfig.setAccessToken es estado global, otro hilo podría cambiarlo
        synchronized (MercadoPagoConfigUtil.class) {
            try {
                payment = new PaymentClient().get(paymentId);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Error al obtener datos del pago " + paymentId + ": " + e.getMessage(), e);
            }

            // Si el pago tiene external reference, configurar el token del comercio
            // y reintentar la llamada con el token correcto si es necesario
            if (payment.getExternalReference() != null) {
                UUID orderId = UUID.fromString(payment.getExternalReference());
                boolean tokenChanged = configureCommerceTokenFromOrder(orderId);

                if (tokenChanged) {
                    try {
                        payment = new PaymentClient().get(paymentId);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Error al obtener datos del pago con token del comercio " + paymentId + ": " + e.getMessage(), e);
                    }
                }
            }
        }

        String externalReference = payment.getExternalReference();
        if (externalReference == null) {
            throw new IllegalArgumentException(
                    "El pago " + paymentId + " no tiene external reference");
        }

        UUID orderId = UUID.fromString(externalReference);

        return Optional.of(new PaymentWebhookData(
                orderId, String.valueOf(paymentId), payment.getStatus()));
    }

    /**
     * Configura el token de MercadoPago del comercio asociado a la orden.
     * Debe llamarse dentro de un bloque synchronized sobre MercadoPagoConfigUtil.class.
     *
     * @return true si el token fue cambiado, false si ya era el correcto o no se encontró la orden
     */
    private boolean configureCommerceTokenFromOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    String token = order.getCommerce().getMercadoPagoAccessToken();
                    return MercadoPagoConfigUtil.configureCommerceToken(token);
                })
                .orElse(false);
    }
}
