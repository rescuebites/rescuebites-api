package com.rescuebites.api.commerce.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.users.controllers.responses.UserResponse;

import java.util.List;
import java.util.UUID;

public record CommerceResponse(
        UUID commerceId,
        String name,
        String description,
        List<CommerceTypeEnum> commerceTypes,
        String openingHours,
        String address,
        String locality,
        String phone,
        List<ImageResponse> images,
        UserResponse user
) {}