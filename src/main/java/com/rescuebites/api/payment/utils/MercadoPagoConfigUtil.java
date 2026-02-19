package com.rescuebites.api.payment.utils;

import com.mercadopago.MercadoPagoConfig;

public class MercadoPagoConfigUtil {

    private MercadoPagoConfigUtil() {
    }

    public static void configureCommerceToken(String commerceAccessToken) {
        if (commerceAccessToken != null && !commerceAccessToken.isBlank()) {
            MercadoPagoConfig.setAccessToken(commerceAccessToken);
        }
    }
}