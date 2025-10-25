package com.rescuebites.api.users.controllers.responses;

import com.rescuebites.api.security.enums.Role;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        Role role,
        boolean enabled
) {}

