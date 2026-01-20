package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.Email;
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

        @Email(message = "El email debe ser válido")
        private String email;

        private String password;
        private String confirmPassword;

        private List<PreferenceType> preferences = Collections.emptyList();
}