package com.rescuebites.api.product.facades.interfaces;

import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.product.data.models.Product;

import java.time.LocalDate;
import java.util.UUID;

public interface IProductValidationFacade {

    /**
     * Busca y retorna un comercio activo por su ID
     * @throws ResourceNotFoundException si el comercio no existe o está inactivo
     */
    Commerce findCommerceById(UUID commerceId);

    /**
     * Busca y retorna un producto por su ID y el ID del comercio
     * @throws ResourceNotFoundException si no hay un producto y un comercio
     */
    Product findProductByIdAndCommerceIdOrThrowException(UUID productId, UUID commerceId);

    /**
     * Valida que la categoría y condición sean compatibles con el tipo de comercio
     * @throws ValidationException si la categoría o condición no son válidas
     */
    void validateCategoryAndCondition(ProductCategory category, ProductCondition condition, Commerce commerce);

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

    /**
     * Valida que al menos un campo del request de actualización esté presente
     * @throws ValidationException si no modificó al menos un campo
     */
    void validateAtLeastOneFieldToUpdate(UpdateProductRequest request);

    /**
     * Valida y procesa las actualizaciones de categoría y condición del producto
     */
    void validateAndProcessCategoryConditionUpdate(
            Product product,
            UpdateProductRequest request
    );
}