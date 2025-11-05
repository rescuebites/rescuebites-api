package com.rescuebites.api.shared.facades.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rescuebites.api.client.repositories.IImageRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ImageUploadException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class ImageFacade implements IImageFacade {

    private final Cloudinary cloudinary;
    private final IImageRepository imageRepository;

    @Override
    public void ifProfilePictureExceedsMaximumSizeThrowException(MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAXIMUM_FILE_SIZE) {
            throw new IllegalArgumentException("La foto no debe superar los 2MB");
        }
    }

    @Override
    public void ifProfilePictureIsNotJpgOrPngThrowException(String contentType) {
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IllegalArgumentException("La foto debe estar en formato JPG o PNG");
        }
    }

    @Override
    public void ifProfilePictureIsMissingThrowException(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Debe carga al menos una foto");
        }
    }

    @Override
    public Image uploadAndSaveImage(MultipartFile multipartFile) {
        try{
            Map<String, Object> uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(),
                    ObjectUtils.asMap("folder", "uploads"));

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
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            imageRepository.deleteByPublicId(publicId);
        } catch (IOException e) {
            throw new ImageUploadException("Error al borrar la imagen", e);
        }
    }
}