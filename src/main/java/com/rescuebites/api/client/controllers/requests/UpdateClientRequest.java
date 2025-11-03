package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.users.utils.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public record UpdateClientRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate birthDate,

        @NotBlank(message = "La dirección es obligatoria")
        String address,

        @NotBlank(message = "El email es obligatorio")
        @ValidEmail
        String email,

        String newPassword,

        String confirmNewPassword,

        List<PreferenceType> preferences
) {}
