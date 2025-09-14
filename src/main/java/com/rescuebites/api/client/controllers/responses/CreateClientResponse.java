package com.rescuebites.api.client.controllers.responses;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateClientResponse(
        UUID clientId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        List<String> preferences,
        List<String> images
) {}