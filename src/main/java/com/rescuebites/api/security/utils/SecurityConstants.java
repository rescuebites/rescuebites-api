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
    };
}