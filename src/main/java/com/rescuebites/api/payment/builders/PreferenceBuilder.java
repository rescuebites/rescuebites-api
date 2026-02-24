package com.rescuebites.api.payment.builders;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.data.models.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rescuebites.api.payment.utils.PaymentConstants.*;

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

    // Construye una preferencia de pago completa a partir de una orden.
    public PreferenceRequest buildPreferenceRequest(Order order) {
        return PreferenceRequest.builder()
                .items(buildPreferenceItems(order))
                .backUrls(buildBackUrls(order))
                .autoReturn(AUTO_RETURN_VALUE)
                .externalReference(order.getOrderId().toString())
                .statementDescriptor(STATEMENT_DESCRIPTOR)
                .payer(buildPayerRequest(order))
                .notificationUrl(webhookUrl)
                .build();
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

        return items;
    }

    // Construye las URLs de redirección (éxito, fallo, pendiente).
    public PreferenceBackUrlsRequest buildBackUrls(Order order) {
        String orderId = "?orderId=" + order.getOrderId();

        return PreferenceBackUrlsRequest.builder()
                .success(successUrl + orderId)
                .failure(failureUrl + orderId)
                .pending(pendingUrl + orderId)
                .build();
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
