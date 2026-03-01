package com.rescuebites.api.order.controllers.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.UUID;

public record CreateOrderRequest (

    @NotNull(message = "El ID del comercio es obligatorio")
    UUID commerceId,

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
    String notes,

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(description = "Horario programado de retiro (requerido si el comercio está cerrado pero reabre más tarde)",
            example = "15:30:00", nullable = true)
    LocalTime scheduledPickupTime
) {}