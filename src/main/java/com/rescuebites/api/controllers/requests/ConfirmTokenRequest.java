package com.rescuebites.api.controllers.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ConfirmTokenRequest (

        @NotBlank(message = "El token es obligatorio")
        UUID token
) {}