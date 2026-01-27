package com.rescuebites.api.commerce.controllers.responses;

import com.rescuebites.api.client.controllers.responses.ImageResponse;

import java.util.List;
import java.util.UUID;

public record CommercePublicResponse(
        UUID commerceId,
        String name,
        List<ImageResponse> images
) {}