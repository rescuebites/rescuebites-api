package com.rescuebites.api.payment.services.mercadopago;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.rescuebites.api.exceptions.custom_exceptions.PaymentException;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.builders.PreferenceBuilder;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.utils.MercadoPagoConfigUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceCreator {

    private final PreferenceBuilder preferenceBuilder;

    public PaymentLinkResponse createPaymentPreference(Order order) {
        // Construir request fuera del lock (no depende del token global)
        PreferenceRequest preferenceRequest = preferenceBuilder.buildPreferenceRequest(order);

        synchronized (MercadoPagoConfigUtil.class) {
            return MercadoPagoConfigUtil.executeWithCommerceToken(
                    order.getCommerce().getMercadoPagoAccessToken(),
                    () -> {
                        try {
                            PreferenceClient client = new PreferenceClient();
                            Preference preference = client.create(preferenceRequest);

                            return new PaymentLinkResponse(
                                    preference.getId(),
                                    preference.getInitPoint(),
                                    preference.getSandboxInitPoint()
                            );
                        } catch (MPApiException e) {
                            String errorMessage = e.getApiResponse() != null ?
                                    e.getApiResponse().getContent() : e.getMessage();
                            throw new PaymentException("Error al crear preferencia de pago: " + errorMessage);
                        } catch (MPException e) {
                            throw new PaymentException("Error al crear preferencia de pago: " + e.getMessage());
                        }
                    }
            );
        }
    }
}
