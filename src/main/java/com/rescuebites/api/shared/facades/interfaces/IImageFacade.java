package com.rescuebites.api.shared.facades.interfaces;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageFacade {
    void ifProfilePictureExceedsMaximumSizeThrowException(MultipartFile multipartFile);

    void ifProfilePictureIsNotJpgOrPngThrowException(String contentType);

    Image uploadAndSaveImage(MultipartFile multipartFile);

    List<Image> uploadAndSaveImages(MultipartFile[] files);

    void deleteImage(String publicId);

    void validateImages(MultipartFile[] images);

    Image replaceImage(Image currentImage, MultipartFile newImage);

    void addImagesToExisting(List<Image> currentImages, MultipartFile[] newImages);
}
