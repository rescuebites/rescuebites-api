package com.rescuebites.api.shared.facades.implementations;

import com.rescuebites.api.client.repositories.IImageRepository;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.commands.ProfilePictureCommand;
import com.rescuebites.api.shared.facades.factories.ImageFactory;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.shared.storage.ImageStorage;
import com.rescuebites.api.shared.storage.models.StoredImage;
import com.rescuebites.api.shared.validation.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ImageFacade implements IImageFacade {

    private final ImageValidator imageValidator;
    private final ImageStorage imageStorage;
    private final ImageFactory imageFactory;
    private final IImageRepository imageRepository;

    @Override
    public Image processProfilePicture(ProfilePictureCommand command) {
        if (imageValidator.shouldSkip(command)) {
            return null;
        }

        imageValidator.validate(command);
        StoredImage storedImage = imageStorage.store(command.file(), command.targetFolder());
        return imageFactory.createFrom(storedImage);
    }

    @Override
    public Image replaceProfilePicture(Image currentImage, ProfilePictureCommand command) {
        if (imageValidator.shouldSkip(command)) {
            return currentImage;
        }

        removeImageIfPresent(currentImage);
        return processProfilePicture(command);
    }

    @Override
    public void removeImage(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return;
        }

        imageStorage.delete(publicId);
        imageRepository.deleteByPublicId(publicId);
    }

    private void removeImageIfPresent(Image currentImage) {
        if (currentImage == null) {
            return;
        }

        removeImage(currentImage.getPublicId());
    }
}