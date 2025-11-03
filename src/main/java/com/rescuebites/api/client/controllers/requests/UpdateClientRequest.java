package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateClientRequest(
        String firstName,
        String lastName,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate birthDate,

        String address,

        UUID userId,

        List<PreferenceType> preferences,

        @Email(message = "El email debe tener un formato válido")
        String email,

        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        String confirmPassword
) {}
