package com.rescuebites.api.controllers.requests;

import com.rescuebites.api.utils.PasswordMatches;
import com.rescuebites.api.utils.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record RegisterRequest (

        @NotBlank(message = "El nombre es obligatorio") //Anotación solo para tipo String
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotBlank(message = "El email es obligatorio")
        @ValidEmail
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 22, message = "La contraseña debe tener entre 8 y 22 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,22}$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial"
        )
        String password,

        @NotBlank(message = "La confirmación de la contraseña es obligatoria")
        String confirmPassword
){}