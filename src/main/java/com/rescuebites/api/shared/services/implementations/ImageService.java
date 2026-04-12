package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.client.repositories.IImageRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.shared.services.interfaces.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final IImageRepository imageRepository;
    private final IImageFacade imageFacade;

    @Override
    @Transactional
    public void deleteImage(UUID imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen", "id", imageId));

        if (image.getProduct() != null) {
            handleProductImageDeletion(image);
        } else if (image.getCommerce() != null) {
            handleCommerceImageDeletion(image);
        } else if (image.getClient() != null) {
            handleClientImageDeletion(image);
        } else {
            throw new ValidationException("La imagen no está asociada a ninguna entidad válida");
        }
    }

    private void handleProductImageDeletion(Image image) {
        var product = image.getProduct();
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        if (product.getImages().size() <= 1) {
            throw new ValidationException("No se puede eliminar la última imagen del producto");
        }

        product.getImages().remove(image);
        reorderPositions(product.getImages());
        imageFacade.deleteImage(image.getPublicId());
    }

    private void handleCommerceImageDeletion(Image image) {
        var commerce = image.getCommerce();
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());

        if (commerce.getImages().size() <= 1) {
            throw new ValidationException("No se puede eliminar la última imagen del comercio");
        }

        commerce.getImages().remove(image);
        reorderPositions(commerce.getImages());
        imageFacade.deleteImage(image.getPublicId());
    }

    private void handleClientImageDeletion(Image image) {
        var client = image.getClient();
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        // Para clientes, la imagen es opcional
        client.setImage(null);
        imageFacade.deleteImage(image.getPublicId());
        imageRepository.delete(image);
    }

    private void reorderPositions(List<Image> images) {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setPosition(i);
        }
    }
}
