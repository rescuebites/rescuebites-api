package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.shared.Image;

import java.util.List;

//Todos los campos son opcionales, porque no necesariamente se deben modificar todos al mismo tiempo
public record UpdateCommerceRequest(
        String name,
        String description,
        List<CommerceType> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        Image image
) {}
