package com.rescuebites.api.product.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para actualizar un producto")
public class UpdateProductRequest{

        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Schema(description = "Nombre del producto", example = "Manzanas Red Delicious")
        private String name;

        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        @Schema(description = "Descripción detallada")
        private String description;

        @Min(value = 0, message = "El stock no puede ser negativo")
        @Schema(description = "Cantidad disponible", example = "50")
        private Integer stock;

        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @DecimalMax(value = "999999.99", message = "El precio no puede superar 999999.99")
        @Schema(description = "Precio original sin descuento", example = "150.00")
        private BigDecimal originalPrice;

        @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
        @Schema(description = "Porcentaje de descuento", example = "30.0")
        private BigDecimal discountPercentage;

        @Schema(description = "Categoría del producto", example = "FRUIT")
        private ProductCategory category;

        @Schema(description = "Estado/condición del producto", example = "RIPE")
        private ProductCondition condition;

        @Future(message = "La fecha de vencimiento debe ser futura")
        @Schema(description = "Fecha de vencimiento (opcional)")
        private LocalDate expirationDate;

        @Schema(description = "Preferencias alimenticias de un cliente")
        private Set<PreferenceType> preferences;
}