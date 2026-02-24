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

            // Búsqueda pública
            "/api/v1/search",

            // Webhook de Mercado Pago (debe estar público para recibir notificaciones)
            "/api/v1/payments/webhook",
    };
}