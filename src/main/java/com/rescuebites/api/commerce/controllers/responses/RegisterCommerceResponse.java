package com.rescuebites.api.commerce.controllers.responses;

import com.rescuebites.api.commerce.data.models.CommerceType;

import java.util.List;

public record RegisterCommerceResponse (
        String name,
        String description,
        List<CommerceType> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        String imageUrl
){}
