package com.rescuebites.api.client.controllers.responses;

import java.util.UUID;

public record ImageResponse(
        UUID imageId,
        String url,
        String publicId
) {}

