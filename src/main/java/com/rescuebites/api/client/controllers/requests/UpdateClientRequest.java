package com.rescuebites.api.client.controllers.requests;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.validation.constraints.PastOrPresent;

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

        List<PreferenceType> preferences
) {}
