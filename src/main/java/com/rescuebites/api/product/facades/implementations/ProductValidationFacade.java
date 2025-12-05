package com.rescuebites.api.product.facades.implementations;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rescuebites.api.product.utils.Constants.MAX_IMAGES;
import static com.rescuebites.api.product.utils.Constants.MIN_IMAGES;

@Service
@RequiredArgsConstructor
public class ProductValidationFacade implements IProductValidationFacade {

    private final ICommerceRepository commerceRepository;
    private final IImageFacade imageFacade;
    //private final Clock clock;

    @Override
    public Commerce findCommerceById(UUID commerceId) {
        return commerceRepository.findByCommerceIdAndActiveTrue(commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Commerce", "id", commerceId));
    }

    @Override
    public void validateCategoryAndCondition(ProductCategory category, ProductCondition condition, Commerce commerce) {

        Set<CommerceTypeEnum> commerceTypes = commerce.getCommerceTypes().stream()
                .map(CommerceType::getName)
                .collect(Collectors.toSet());

        // Validar categoría
        boolean categoryAllowed = commerceTypes.stream()
                .anyMatch(category::isAllowedFor);

        if (!categoryAllowed) {
            throw new ValidationException(
                    String.format("La categoría '%s' no es válida para este tipo de comercio",
                            category.getDisplayName())
            );
        }

        // Validar condición
        boolean conditionAllowed = commerceTypes.stream()
                .anyMatch(condition::isAllowedFor);

        if (!conditionAllowed) {
            throw new ValidationException(
                    String.format("La condición '%s' no es válida para este tipo de comercio",
                            condition.getDisplayName())
            );
        }
    }

    @Override
    public void validateImages(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            throw new ValidationException(
                    String.format("Debe cargar al menos %d imagen del producto", MIN_IMAGES)
            );
        }

        if (images.length > MAX_IMAGES) {
            throw new ValidationException(
                    String.format("No puede cargar más de %d imágenes", MAX_IMAGES)
            );
        }

        Arrays.stream(images).forEach(image -> {
            if (image == null || image.isEmpty()) {
                throw new ValidationException("Todas las imágenes del producto deben ser válidas");
            }
            imageFacade.ifProfilePictureExceedsMaximumSizeThrowException(image);
            imageFacade.ifProfilePictureIsNotJpgOrPngThrowException(image.getContentType());
        });
    }

    @Override
    public void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        if (expirationDate.isBefore(today)) {
            throw new ValidationException("La fecha de vencimiento no puede ser anterior a hoy");
        }
    }

    @Override
    public void validateCommerceExists(UUID commerceId) {
        if (!commerceRepository.existsById(commerceId)) {
            throw new ResourceNotFoundException("Commerce", "id", commerceId);
        }
    }
}
