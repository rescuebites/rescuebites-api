package com.rescuebites.api.users.controllers.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmTokenRequest (
        @NotNull(message = "El token es obligatorio")
        UUID token
) {}