package com.rescuebites.api.commerce.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;

import java.util.List;

public record CommerceResponse(
        String name,
        String description,
        List<CommerceTypeEnum> commerceTypes,
        List<BusinessHoursResponse> businessHours,
        String address,
        String locality,
        String phone,
        List<ImageResponse> images
) {}