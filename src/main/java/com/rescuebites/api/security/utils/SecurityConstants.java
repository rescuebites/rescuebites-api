package com.rescuebites.api.security.utils;

public class SecurityConstants {

    private SecurityConstants() {}

    public static final String[] WHITELIST = {
            // Autenticación
            "/auth/register",
            "/auth/login",

            // Verificación de cuenta y recuperación de contraseña
            "/api/users/*/verify-account",
            "/api/users/resend-verification-account",
            "/api/users/reset-password/**",

            // Endpoints públicos (home, comercios, productos)
            "/api/v1/public/**",

            // Búsqueda pública
            "/api/v1/search",
            "/api/v1/search/**",

            // Webhook y URLs de retorno de Mercado Pago
            "/api/v1/payments/webhook",
            "/api/v1/payments/success",
            "/api/v1/payments/failure",
            "/api/v1/payments/pending",
    };
}