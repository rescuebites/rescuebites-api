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

    /**
     * Valida que las imágenes cumplan con los requisitos (cantidad, tamaño, formato)
     * @throws ValidationException si las imágenes no son válidas
     */
    void validateImages(MultipartFile[] images);

    /**
     * Procesa y actualiza las imágenes de una entidad.
     * Elimina las imágenes antiguas y sube las nuevas.
     * @param currentImages Lista actual de imágenes de la entidad
     * @param newImages Nuevas imágenes a cargar
     * @return Lista de nuevas imágenes procesadas y almacenadas
     * @throws ValidationException si las imágenes no son válidas
     */
    List<Image> processAndUpdateImages(List<Image> currentImages, MultipartFile[] newImages);

    Image replaceImage(Image currentImage, MultipartFile newImage);
}
