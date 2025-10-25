package com.rescuebites.api.security.utils;

public class SecurityConstants {

    private SecurityConstants() {}

    public static final String[] WHITELIST = {
            "/auth/login",
            "/api/users/reset-password/**",
            "/api/users/*/verify-account",
            "/api/v1/clients",
            "/api/v1/clients/**",
            "/auth/register"
    };

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

}