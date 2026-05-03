package com.rescuebites.api.order.controllers.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrderRequest (

    @NotNull(message = "El ID del comercio es obligatorio")
    UUID commerceId,

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
    String notes
) {}