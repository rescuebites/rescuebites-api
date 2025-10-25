package com.rescuebites.api.client.controllers.responses;

import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
        UUID clientId,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Image image,
        String address,
        User user,
        List<String> preferences
) {}