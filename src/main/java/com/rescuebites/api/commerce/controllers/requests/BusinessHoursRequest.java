package com.rescuebites.api.commerce.controllers.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHoursRequest {

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek dayOfWeek;

    private boolean closed;

    // Hora de apertura (mañana o turno único)
    private LocalTime openTime;

    // Hora de cierre (mañana o turno único)
    private LocalTime closeTime;

    // Hora de apertura del turno tarde (opcional)
    private LocalTime afternoonOpenTime;

    // Hora de cierre del turno tarde (opcional)
    private LocalTime afternoonCloseTime;
}
