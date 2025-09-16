package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.models.CommerceType;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RegisterCommerceRequest(
        String name,
        String description,
        List<CommerceType> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        String imageUrl
){}

