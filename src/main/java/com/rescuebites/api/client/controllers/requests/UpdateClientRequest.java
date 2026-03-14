package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientRequest {

        private String firstName;

        private String lastName;

        private LocalDate birthDate;

        private String address;

        @Pattern(
                regexp = "^\\+54(9)?[0-9]{10}$",
                message = "El número de celular debe tener el formato válido argentino, ej: +54911XXXXXXXX"
        )
        private String phone;

        @Email(message = "El email debe ser válido")
        private String email;

        private String password;

        private String confirmPassword;

        private List<PreferenceType> preferences = Collections.emptyList();

        private String locality;
}