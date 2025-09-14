package com.rescuebites.api.services.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IImageRepository;
import com.rescuebites.api.data.models.Image;
import com.rescuebites.api.exceptions.custom_exceptions.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;
    private final IImageRepository imageRepository;

    public Image uploadAndSaveImage(MultipartFile file)  {

        try{
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
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

    public List<Image> uploadAndSaveImages(MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadAndSaveImage)
                .toList();
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            imageRepository.deleteByPublicId(publicId);
        } catch (IOException e) {
            throw new ImageUploadException("Error al borrar la imagen", e);
        }
    }

}
