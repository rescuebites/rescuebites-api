package com.rescuebites.api.users.controllers.requests;

public record LoginRequest (
        String email,
        String password
){}