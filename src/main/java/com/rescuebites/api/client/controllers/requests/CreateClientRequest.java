package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

        @NotBlank(message = "El nombre es obligatorio")
        private String firstName;

        @NotBlank(message = "El apellido es obligatorio")
        private String lastName;

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        private LocalDate birthDate;

        @NotBlank(message = "La dirección es obligatoria")
        private String address;

        @NotNull(message = "El usuario es obligatorio")
        private UUID userId;

        private List<PreferenceType> preferences = Collections.emptyList();
}