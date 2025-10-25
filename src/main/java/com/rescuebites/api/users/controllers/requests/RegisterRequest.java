package com.rescuebites.api.users.controllers.requests;

import com.rescuebites.api.security.enums.Role;
import com.rescuebites.api.users.utils.PasswordMatches;
import com.rescuebites.api.users.utils.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record RegisterRequest (

        @NotBlank(message = "El email es obligatorio")
        @ValidEmail
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,22}$",
                message = "La contraseña debe tener entre 8 y 22 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un caracter especial"
        )
        String password,

        @NotBlank(message = "La confirmación de la contraseña es obligatoria")
        String confirmPassword,

        Role role
){}