package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.enums.ProductCheckType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "El stock es obligatorio")
        @Positive(message = "El stock debe ser mayor a cero")
        Integer stock,

        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio original debe ser mayor a 0")
        BigDecimal originalPrice,

        @NotNull(message = "El porcentaje de descuento es obligatorio")
        @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
        BigDecimal discountPercentage,

        @NotEmpty(message = "Debe seleccionar al menos un check")
        List<ProductCheckType> checks,

        @FutureOrPresent(message = "La fecha de vencimiento no puede ser anterior a hoy")
        LocalDate expirationDate
) {}
