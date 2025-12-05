package com.rescuebites.api.product.controllers.requests;

import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request para actualizar un producto")
public record UpdateProductRequest(

        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Schema(description = "Nombre del producto", example = "Manzanas Red Delicious")
        String name,

        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        @Schema(description = "Descripción detallada")
        String description,

        @Min(value = 0, message = "El stock no puede ser negativo")
        @Schema(description = "Cantidad disponible", example = "50")
        Integer stock,

        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @DecimalMax(value = "999999.99", message = "El precio no puede superar 999999.99")
        @Schema(description = "Precio original sin descuento", example = "150.00")
        BigDecimal originalPrice,

        @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
        @Schema(description = "Porcentaje de descuento", example = "30.0")
        BigDecimal discountPercentage,

        @Schema(description = "Categoría del producto", example = "FRUIT")
        ProductCategory category,

        @Schema(description = "Estado/condición del producto", example = "RIPE")
        ProductCondition condition,

        @Future(message = "La fecha de vencimiento debe ser futura")
        @Schema(description = "Fecha de vencimiento (opcional)")
        LocalDate expirationDate
) {
}
