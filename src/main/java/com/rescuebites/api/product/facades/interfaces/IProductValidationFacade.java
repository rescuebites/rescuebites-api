package com.rescuebites.api.product.facades.interfaces;

import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

public interface IProductValidationFacade {

    /**
     * Busca y retorna un comercio activo por su ID
     * @throws ResourceNotFoundException si el comercio no existe o está inactivo
     */
    Commerce findCommerceById(UUID commerceId);

    /**
     * Valida que la categoría y condición sean compatibles con el tipo de comercio
     * @throws ValidationException si la categoría o condición no son válidas
     */
    void validateCategoryAndCondition(ProductCategory category, ProductCondition condition, Commerce commerce);

    /**
     * Valida que las imágenes cumplan con los requisitos (cantidad, tamaño, formato)
     * @throws ValidationException si las imágenes no son válidas
     */
    void validateImages(MultipartFile[] images);

    /**
     * Valida que la fecha de vencimiento sea futura
     * @throws ValidationException si la fecha es pasada
     */
    void validateExpirationDate(LocalDate expirationDate);

    /**
     * Valida que el comercio existe
     * @throws ResourceNotFoundException si el comercio no existe
     */
    void validateCommerceExists(UUID commerceId);
}
