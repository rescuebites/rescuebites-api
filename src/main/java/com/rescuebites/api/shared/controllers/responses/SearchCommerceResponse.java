package com.rescuebites.api.shared.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Respuesta de comercio para resultados de búsqueda")
public record SearchCommerceResponse(
        @Schema(description = "ID del comercio")
        UUID commerceId,

        @Schema(description = "Nombre del comercio")
        String name,

        @Schema(description = "Dirección del comercio")
        String address,

        @Schema(description = "Localidad del comercio")
        String locality,

        @Schema(description = "Tipo de comercio")
        CommerceTypeEnum commerceType,

        @Schema(description = "Imágenes del comercio")
        List<ImageResponse> images
) {}
