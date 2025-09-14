package com.rescuebites.api.controllers.responses;

import com.rescuebites.api.security.enums.Role;

import java.util.UUID;

public record AuthResponse (
        UUID userId,
        String email,
        String token,
        Role role
) {}