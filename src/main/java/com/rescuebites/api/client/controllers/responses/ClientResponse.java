package com.rescuebites.api.client.controllers.responses;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.users.controllers.responses.UserResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
        UUID clientId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        ImageResponse image,
        String address,
        String phone,
        UserResponse user,
        List<PreferenceType> preferences
) {}
