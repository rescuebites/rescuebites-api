package com.rescuebites.api.shared.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rescuebites.api.exceptions.custom_exceptions.ImageUploadException;
import com.rescuebites.api.shared.storage.models.StoredImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryImageStorage implements ImageStorage {

    private final Cloudinary cloudinary;

    @Override
    public StoredImage store(MultipartFile file, String folder) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap("folder", folder));

            return new StoredImage(
                    uploadResult.get("url").toString(),
                    uploadResult.get("public_id").toString()
            );
        } catch (IOException e) {
            throw new ImageUploadException("Error al subir la imagen", e);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ImageUploadException("Error al borrar la imagen", e);
        }
    }
}
