package com.rescuebites.api.shared.facades.interfaces;

import com.rescuebites.api.shared.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageFacade {
    void ifProfilePictureExceedsMaximumSizeThrowException(MultipartFile multipartFile);

    void ifProfilePictureIsNotJpgOrPngThrowException(String contentType);

    void ifProfilePictureIsMissingThrowException(MultipartFile multipartFile);

    Image uploadAndSaveImage(MultipartFile multipartFile);

    List<Image> uploadAndSaveImages(MultipartFile[] files);

    void deleteImage(String publicId);
}
