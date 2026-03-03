package com.rescuebites.api.payment.utils;

import com.mercadopago.MercadoPagoConfig;

import java.util.function.Supplier;

public class MercadoPagoConfigUtil {

    private MercadoPagoConfigUtil() {
    }

    /**
     * Ejecuta una acción con el token de un comercio específico,
     * restaurando el token original al finalizar.
     * Debe usarse dentro de un bloque synchronized sobre MercadoPagoConfigUtil.class.
     *
     * @param commerceAccessToken Token del comercio
     * @param action Acción a ejecutar con el token configurado
     * @return El resultado de la acción
     */
    public static <T> T executeWithCommerceToken(String commerceAccessToken, Supplier<T> action) {
        String previousToken = MercadoPagoConfig.getAccessToken();
        try {
            configureCommerceToken(commerceAccessToken);
            return action.get();
        } finally {
            MercadoPagoConfig.setAccessToken(previousToken);
        }
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