package com.rescuebites.api.payment.builders;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.data.models.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import static com.rescuebites.api.payment.utils.PaymentConstants.*;
import static com.rescuebites.api.cart.utils.CartConstants.SERVICE_FEE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreferenceBuilder {

    @Value("${mercadopago.success-url}")
    private String successUrl;

    @Value("${mercadopago.failure-url}")
    private String failureUrl;

    @Value("${mercadopago.pending-url}")
    private String pendingUrl;

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    // Construye una preferencia de pago usando frontendBaseUrl para las back_urls.
    public PreferenceRequest buildPreferenceRequest(Order order, String frontendBaseUrl) {
        PreferenceBackUrlsRequest backUrls = buildBackUrls(order, frontendBaseUrl);

        // Construye la solicitud y establece autoReturn solo si hay una URL de éxito válida
        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(buildPreferenceItems(order))
                .backUrls(backUrls)
                .externalReference(order.getOrderId().toString())
                .statementDescriptor(STATEMENT_DESCRIPTOR)
                .payer(buildPayerRequest(order))
                .notificationUrl(webhookUrl);

        if (backUrls != null && backUrls.getSuccess() != null && !backUrls.getSuccess().isBlank()) {
            String success = backUrls.getSuccess();
            boolean isLocal = success.startsWith("http://localhost") || success.startsWith("http://127.0.0.1");
            boolean isHttp = success.startsWith("http://") || success.startsWith("https://");
            if (!isLocal && isHttp) {
                builder.autoReturn(AUTO_RETURN_VALUE);
            } else {
                // Omitir auto_return para URLs locales o no-HTTP para evitar errores de Mercado Pago
                log.debug("Omitiendo auto_return para back_urls.success='{}' (isLocal={}, isHttp={})", success, isLocal, isHttp);
            }
        }

        // Loguear totales del pedido para confirmar que la preferencia incluirá
        // el subtotal, la tarifa de servicio y el total esperado.
        try {
            log.debug("PreferenceBuilder: orderId={}, subtotal={}, serviceFee={}, total= {}",
                    order.getOrderId(), order.getSubtotal(), SERVICE_FEE, order.getTotal());
        } catch (Exception e) {
            log.debug("No se pudieron leer totales del pedido para logging: {}", e.getMessage());
        }

        return builder.build();
    }

    // Construye una preferencia de pago usando las URLs configuradas por defecto.
    public PreferenceRequest buildPreferenceRequest(Order order) {
        return buildPreferenceRequest(order, null);
    }

    // Construye los items de la preferencia desde los items de la orden.
    public List<PreferenceItemRequest> buildPreferenceItems(Order order) {
        List<PreferenceItemRequest> items = new ArrayList<>(order.getItems().size());

        for (OrderItem orderItem : order.getItems()) {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(orderItem.getProduct().getProductId().toString())
                    .title(orderItem.getProductName())
                    .description(buildItemDescription(order))
                    .quantity(orderItem.getQuantity())
                    .currencyId(CURRENCY_ID)
                    .unitPrice(orderItem.getUnitPrice())
                    .build();

            items.add(item);
        }

        // Agregar la tarifa de servicio como un item separado si aplica, para que
        // Mercado Pago cobre el monto total incluyendo la comisión/servicio.
        try {
            BigDecimal serviceFee = SERVICE_FEE;
            if (serviceFee.compareTo(BigDecimal.ZERO) > 0) {
                PreferenceItemRequest feeItem = PreferenceItemRequest.builder()
                        .id("service-fee")
                        .title("Tarifa de servicio")
                        .description("Tarifa por servicio de la plataforma")
                        .quantity(1)
                        .currencyId(CURRENCY_ID)
                        .unitPrice(serviceFee)
                        .build();

                items.add(feeItem);
            }
        } catch (Exception e) {
            // No detener la generación de la preferencia si por alguna razón no
            // se puede leer la tarifa; registrar y continuar con los items de productos.
            log.debug("No se pudo agregar tarifa de servicio a la preferencia: {}", e.getMessage());
        }

        return items;
    }

    // Construye las URLs de redirección usando frontendBaseUrl si se provee, o las configuradas por defecto.
    public PreferenceBackUrlsRequest buildBackUrls(Order order, String frontendBaseUrl) {
        String orderId = "?orderId=" + order.getOrderId();

        // Construir las back_urls. Si el frontend proporcionó su origen (incluso
        // localhost en desarrollo), preferimos usarlo para que el enlace "Volver a la tienda"
        // lleve directamente al cliente y evite pasar por el túnel de ngrok (que en cuentas
        // free muestra una interstitial al visitante).
        boolean hasFrontend = frontendBaseUrl != null && !frontendBaseUrl.isBlank();

        String resolvedSuccess;
        String resolvedFailure;
        String resolvedPending;

        if (hasFrontend) {
            // Apuntar a las rutas de resultado del frontend y adjuntar orderId como query.
            resolvedSuccess = frontendBaseUrl + "/payment/success" + orderId;
            resolvedFailure = frontendBaseUrl + "/payment/failure" + orderId;
            resolvedPending = frontendBaseUrl + "/payment/pending" + orderId;
        } else {
            // Usar las URLs configuradas en el servidor (production / staging).
            resolvedSuccess = successUrl + orderId;
            resolvedFailure = failureUrl + orderId;
            resolvedPending = pendingUrl + orderId;
        }

        return PreferenceBackUrlsRequest.builder()
                .success(resolvedSuccess)
                .failure(resolvedFailure)
                .pending(resolvedPending)
                .build();
    }

    // Construye las URLs de redirección usando las URLs configuradas por defecto.
    public PreferenceBackUrlsRequest buildBackUrls(Order order) {
        return buildBackUrls(order, null);
    }

    // Construye los datos del pagador desde el cliente de la orden.
    public PreferencePayerRequest buildPayerRequest(Order order) {
        return PreferencePayerRequest.builder()
                .name(order.getClient().getFirstName())
                .surname(order.getClient().getLastName())
                .email(order.getClient().getUser().getEmail())
                .build();
    }

    private String buildItemDescription(Order order) {
        return "Producto de " + order.getCommerce().getName();
    }
}
