package com.rescuebites.api.client.data.mappers;

import com.rescuebites.api.client.controllers.responses.ImageResponse;
import com.rescuebites.api.shared.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public static ImageResponse toImageResponse(Image image) {
        return new ImageResponse(
                image.getImageId(),
                image.getUrl(),
                image.getPublicId()
        );
    }
}
