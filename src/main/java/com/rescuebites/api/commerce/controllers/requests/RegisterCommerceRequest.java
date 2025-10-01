package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.shared.Image;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record RegisterCommerceRequest(
        UUID userId,
        String name,
        String description,
        List<UUID> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        Image image
){}

