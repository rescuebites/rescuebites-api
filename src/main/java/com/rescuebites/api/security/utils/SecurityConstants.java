package com.rescuebites.api.security.utils;

public class SecurityConstants {

    private SecurityConstants() {}

    public static final String[] WHITELIST = {
            "/auth/login",
            "/api/users/reset-password/**",
            "/auth/register",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/favicon.ico"
    };

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

}