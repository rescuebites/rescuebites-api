package com.rescuebites.api.controllers.requests;

public record LoginRequest (
        String email,
        String password
){}