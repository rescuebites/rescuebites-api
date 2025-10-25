package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateClientRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate birthDate,

        @NotBlank(message = "La dirección es obligatoria")
        String address,

        @NotNull(message = "El usuario es obligatorio")
        UUID userId,

        List<PreferenceType> preferences
) {}