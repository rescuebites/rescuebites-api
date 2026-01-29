package com.rescuebites.api.product.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Respuesta con información del producto")
public record ProductResponse(

        @Schema(description = "ID del producto")
        UUID productId,

        @Schema(description = "ID del comercio")
        UUID commerceId,

        @Schema(description = "Nombre del comercio")
        String commerceName,

        @Schema(description = "Nombre del producto")
        String name,

        @Schema(description = "Descripción del producto")
        String description,

        @Schema(description = "Stock disponible")
        Integer stock,

        @Schema(description = "Precio original")
        BigDecimal originalPrice,

        @Schema(description = "Porcentaje de descuento")
        BigDecimal discountPercentage,

        @Schema(description = "Precio con descuento aplicado")
        BigDecimal discountedPrice,

        @Schema(description = "Categoría del producto")
        ProductCategory category,

        @Schema(description = "Condición del producto")
        ProductCondition condition,

        @Schema(description = "Nombre legible de la condición")
        String conditionDisplayName,

        @Schema(description = "Fecha de vencimiento")
        LocalDate expirationDate,

        @Schema(description = "URLs de las imágenes del producto")
        List<ImageResponse> images,

        @Schema(description = "Indica si el producto está activo")
        Boolean active,

        @Schema(description = "Preferencias alimenticias de un cliente")
        List<PreferenceType> preferences
) {
}