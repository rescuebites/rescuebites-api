package com.rescuebites.api.shared.controllers.implementations;

import com.rescuebites.api.shared.controllers.interfaces.IImageController;
import com.rescuebites.api.shared.services.interfaces.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ImageController implements IImageController {

    private final IImageService imageService;

    @Override
    public void deleteImage(UUID imageId) {
        imageService.deleteImage(imageId);
    }
}
