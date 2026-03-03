package com.rescuebites.api.payment.utils;

import com.mercadopago.MercadoPagoConfig;

public class MercadoPagoConfigUtil {

    private MercadoPagoConfigUtil() {
    }

    /**
     * Configura el token de acceso de MercadoPago para el comercio.
     *
     * @return true si el token fue cambiado, false si era el mismo o era inválido
     */
    public static synchronized boolean configureCommerceToken(String commerceAccessToken) {
        if (commerceAccessToken != null && !commerceAccessToken.isBlank()) {
            String currentToken = MercadoPagoConfig.getAccessToken();
            if (!commerceAccessToken.equals(currentToken)) {
                MercadoPagoConfig.setAccessToken(commerceAccessToken);
                return true;
            }
        }
        return false;
    }
}