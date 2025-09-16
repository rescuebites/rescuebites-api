package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.models.CommerceType;
import java.util.List;

//Todos los campos son opcionales, porque no necesariamente se deben modificar todos al mismo tiempo
public record UpdateCommerceRequest(
        String name,
        String description,
        List<CommerceType> commerceTypes,
        String openingHours,
        String direction,
        String phone,
        String imageUrl // Ahora solo un String (URL), sin archivos
) {}
