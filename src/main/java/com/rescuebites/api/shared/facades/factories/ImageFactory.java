package com.rescuebites.api.shared.facades.factories;

import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.storage.models.StoredImage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageFactory {

    public Image createFrom(StoredImage storedImage) {
        return Image.builder()
                .imageId(UUID.randomUUID())
                .url(storedImage.url())
                .publicId(storedImage.publicId())
                .build();
    }
}
