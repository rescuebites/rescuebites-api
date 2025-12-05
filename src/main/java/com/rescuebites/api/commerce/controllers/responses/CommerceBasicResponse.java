package com.rescuebites.api.commerce.controllers.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Información básica del comercio")
public record CommerceBasicResponse(

        @Schema(description = "ID del comercio")
        UUID commerceId,

        @Schema(description = "Nombre del comercio")
        String name,

        @Schema(description = "Dirección del comercio")
        String address,

        @Schema(description = "Teléfono del comercio")
        String phoneNumber
) {
}