package com.rescuebites.api.product.facades.implementations;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductValidationFacade implements IProductValidationFacade {

    private final ICommerceRepository commerceRepository;
    private final IProductRepository productRepository;

    @Override
    public Product findProductByIdAndCommerceIdOrThrowException(
            UUID productId,
            UUID commerceId
    ) {
        return productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @Override
    public void validateCategoryAndCondition(
            ProductCategory category,
            ProductCondition condition,
            Commerce commerce
    ) {

        // Obtengo el tipo de comercio principal
        CommerceTypeEnum commerceType = commerce.getCommerceTypes()
                .stream()
                .findFirst()
                .map(CommerceType::getName)
                .orElseThrow(() -> new ValidationException("El comercio no tiene un tipo asignado"));

        if (!ProductCategory.getAllowedFor(commerceType).contains(category)) {
            throw new ValidationException(
                    String.format("La categoría '%s' no es válida para este tipo de comercio",
                            category.getDisplayName())
            );
        }

        if (!ProductCondition.getAllowedFor(commerceType).contains(condition)) {
            throw new ValidationException(
                    String.format("La condición '%s' no es válida para este tipo de comercio",
                            condition.getDisplayName())
            );
        }
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

    @Override
    public void validateAtLeastOneFieldToUpdate(UpdateProductRequest request) {
        boolean hasAtLeastOneField = StringUtils.hasText(request.getName()) ||
                StringUtils.hasText(request.getDescription()) ||
                // Stock es válido si NO es null (incluyendo 0)
                request.getStock() != null ||
                request.getOriginalPrice() != null ||
                request.getDiscountPercentage() != null ||
                request.getCategory() != null ||
                request.getCondition() != null ||
                request.getExpirationDate() != null ||
                (request.getPreferences() != null && !request.getPreferences().isEmpty());

        if (!hasAtLeastOneField) {
            throw new ValidationException("Debe modificar al menos un campo del producto");
        }
    }

    @Override
    public void validateAndProcessCategoryConditionUpdate(Product product, UpdateProductRequest request) {
        if (request.getCategory() == null && request.getCondition() == null) {
            return;
        }

        // Determinar qué valores usar para la validación
        ProductCategory categoryToValidate = request.getCategory() != null ?
                request.getCategory() : product.getCategory();

        ProductCondition conditionToValidate = request.getCondition() != null ?
                request.getCondition() : product.getCondition();

        validateCategoryAndCondition(
                categoryToValidate,
                conditionToValidate,
                product.getCommerce()
        );
    }
}