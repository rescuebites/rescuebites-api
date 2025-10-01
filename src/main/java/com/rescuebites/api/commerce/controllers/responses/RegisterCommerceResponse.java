package com.rescuebites.api.commerce.controllers.responses;

import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.shared.Image;

import java.util.List;
import java.util.UUID;

public record RegisterCommerceResponse (
        UUID userId,
        String name,
        String description,
        List<CommerceType> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        Image image
){}
