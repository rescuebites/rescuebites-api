package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.users.utils.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

//@PasswordMatches
public record UpdateClientRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        LocalDate birthDate,

        String address,

        List<PreferenceType> preferences,

        @ValidEmail
        String email,

        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,22}$",
                message = "La contraseña debe tener entre 8 y 22 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un caracter especial"
        )
        String password,

        String confirmPassword
) {}
