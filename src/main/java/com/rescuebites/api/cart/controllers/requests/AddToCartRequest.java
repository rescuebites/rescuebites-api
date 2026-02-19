package com.rescuebites.api.cart.controllers.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddToCartRequest (

    @NotNull(message = "El ID del producto es obligatorio")
    UUID productId,

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    Integer quantity
) {}