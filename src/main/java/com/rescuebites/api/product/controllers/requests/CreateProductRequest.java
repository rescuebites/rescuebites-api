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
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear un producto")
public class CreateProductRequest{

        @NotBlank(message = "El nombre del producto es requerido")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Schema(description = "Nombre del producto", example = "Manzanas Red Delicious")
        private String name;

        @NotBlank(message = "La descripción es requerida")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        @Schema(description = "Descripción detallada", example = "Manzanas frescas de la región")
        private String description;

        @NotNull(message = "El stock es requerido")
        @Min(value = 1, message = "El stock debe ser al menos 1")
        @Schema(description = "Cantidad disponible", example = "50")
        private Integer stock;

        @NotNull(message = "El precio original es requerido")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @DecimalMax(value = "999999.99", message = "El precio no puede superar 999999.99")
        @Schema(description = "Precio original sin descuento", example = "150.00")
        private BigDecimal originalPrice;

        @NotNull(message = "El porcentaje de descuento es requerido")
        @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
        @Schema(description = "Porcentaje de descuento", example = "30.0")
        private BigDecimal discountPercentage;

        @NotNull(message = "La categoría es requerida")
        @Schema(description = "Categoría del producto", example = "FRUIT")
        private ProductCategory category;

        @NotNull(message = "La condición del producto es requerida")
        @Schema(description = "Estado/condición del producto", example = "RIPE")
        private ProductCondition condition;

        @Future(message = "La fecha de vencimiento debe ser futura")
        @Schema(description = "Fecha de vencimiento (opcional)", example = "2025-12-31")
        private LocalDate expirationDate;

        @Schema(description = "Preferencias alimenticias")
        private List<PreferenceType> preferences = Collections.emptyList();
}