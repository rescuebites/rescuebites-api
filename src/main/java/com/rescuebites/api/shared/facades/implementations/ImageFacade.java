package com.rescuebites.api.shared.facades.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rescuebites.api.client.repositories.IImageRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ImageUploadException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;
import static com.rescuebites.api.product.utils.Constants.MAX_IMAGES;
import static com.rescuebites.api.product.utils.Constants.MIN_IMAGES;

@Service
@RequiredArgsConstructor
public class ImageFacade implements IImageFacade {

    private final Cloudinary cloudinary;
    private final IImageRepository imageRepository;

    @Override
    public void ifProfilePictureExceedsMaximumSizeThrowException(MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAXIMUM_FILE_SIZE) {
            throw new ValidationException("La foto no debe superar los 2MB");
        }
    }

    @Override
    public void ifProfilePictureIsNotJpgOrPngThrowException(String contentType) {
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new ValidationException("La foto debe estar en formato JPG o PNG");
        }
    }

    @Override
    public Image uploadAndSaveImage(MultipartFile multipartFile) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    multipartFile.getBytes(),
                    ObjectUtils.asMap("folder", "uploads")
            );

            return Image.builder()
                    .imageId(UUID.randomUUID())
                    .url(uploadResult.get("url").toString())
                    .publicId(uploadResult.get("public_id").toString())
                    .build();

        } catch (IOException e) {
            throw new ImageUploadException("Error al subir la imagen", e);
        }
    }

    @Override
    public List<Image> uploadAndSaveImages(MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadAndSaveImage)
                .toList();
    }

    @Override
    public void deleteImage(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return; // Si no tiene publicId (imagen default), no hacer nada
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            imageRepository.deleteByPublicId(publicId);
        } catch (IOException e) {
            throw new ImageUploadException("Error al borrar la imagen", e);
        }
    }

    @Override
    public void validateImages(MultipartFile[] images) {
        if (images == null || images.length == 0 ||
                Arrays.stream(images).allMatch(img -> img == null || img.isEmpty())) {
            throw new ValidationException(
                    String.format("Debe cargar al menos %d imagen", MIN_IMAGES)
            );
        }

        if (images.length > MAX_IMAGES) {
            throw new ValidationException(
                    String.format("No puede cargar más de %d imágenes", MAX_IMAGES)
            );
        }

        Arrays.stream(images).forEach(image -> {
            if (image == null || image.isEmpty()) {
                throw new ValidationException("Todas las imágenes deben ser válidas");
            }
            ifProfilePictureExceedsMaximumSizeThrowException(image);
            ifProfilePictureIsNotJpgOrPngThrowException(image.getContentType());
        });
    }

    @Override
    public List<Image> processAndUpdateImages(List<Image> currentImages, MultipartFile[] newImages) {
        validateImages(newImages);

        // Subir nuevas primero (si falla, no perdemos las anteriores)
        List<Image> uploadedImages = uploadAndSaveImages(newImages);

        // Borrar anteriores solo si la subida fue exitosa
        if (currentImages != null && !currentImages.isEmpty()) {
            List<String> publicIdsToDelete = currentImages.stream()
                    .filter(image -> StringUtils.hasText(image.getPublicId()))
                    .map(Image::getPublicId)
                    .toList();

            currentImages.clear();
            publicIdsToDelete.forEach(this::deleteImage);
        }

        return uploadedImages;
    }

    @Override
    public Image replaceImage(Image currentImage, MultipartFile newImage) {
        if (newImage == null || newImage.isEmpty()) {
            return currentImage;
        }

        ifProfilePictureIsNotJpgOrPngThrowException(newImage.getContentType());
        ifProfilePictureExceedsMaximumSizeThrowException(newImage);

        // Subir nueva primero (si falla, no perdemos la anterior)
        Image uploadedImage = uploadAndSaveImage(newImage);

        // Borrar anterior solo si la subida fue exitosa
        if (currentImage != null && StringUtils.hasText(currentImage.getPublicId())) {
            deleteImage(currentImage.getPublicId());
        }

        return uploadedImage;
    }
}