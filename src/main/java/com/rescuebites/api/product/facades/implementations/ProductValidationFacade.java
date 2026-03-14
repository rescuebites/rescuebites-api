package com.rescuebites.api.product.facades.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
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
import java.util.EnumSet;
import java.util.Set;
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
    public void validateCategoryAndConditions(
            ProductCategory category,
            Set<ProductCondition> conditions,
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

        EnumSet<ProductCondition> allowedConditions = ProductCondition.getAllowedFor(commerceType);
        for (ProductCondition condition : conditions) {
            if (!allowedConditions.contains(condition)) {
                throw new ValidationException(
                        String.format("La condición '%s' no es válida para este tipo de comercio",
                                condition.getDisplayName())
                );
            }
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
                (request.getConditions() != null && !request.getConditions().isEmpty()) ||
                request.getExpirationDate() != null ||
                (request.getPreferences() != null);

        if (!hasAtLeastOneField) {
            throw new ValidationException("Debe modificar al menos un campo del producto");
        }
    }

    @Override
    public void validateAndProcessCategoryConditionUpdate(Product product, UpdateProductRequest request) {
        if (request.getCategory() == null && (request.getConditions() == null || request.getConditions().isEmpty())) {
            return;
        }

        // Determinar qué valores usar para la validación
        ProductCategory categoryToValidate = request.getCategory() != null ?
                request.getCategory() : product.getCategory();

        Set<ProductCondition> conditionsToValidate = (request.getConditions() != null && !request.getConditions().isEmpty()) ?
                request.getConditions() : product.getConditions();

        validateCategoryAndConditions(
                categoryToValidate,
                conditionsToValidate,
                product.getCommerce()
        );
    }

    @Override
    public void validateNoIdenticalProductInCommerceForCreate(UUID commerceId, CreateProductRequest request) {
        if (request == null) {
            return;
        }

        var preferences = request.getPreferences() != null ? request.getPreferences().stream().distinct().toList() : java.util.List.<PreferenceType>of();
        long size = preferences.size();
        boolean empty = size == 0;

        boolean exists = productRepository.existsIdenticalProductInCommerce(
                commerceId,
                request.getName() == null ? null : request.getName().trim(),
                request.getCategory(),
                request.getExpirationDate(),
                request.getOriginalPrice(),
                request.getDiscountPercentage(),
                preferences,
                size,
                empty
        );

        if (exists) {
            throw new ValidationException(
                    "Ya existe un producto idéntico registrado en este comercio (mismo nombre, categoría, preferencias, vencimiento y precio)."
            );
        }
    }

    @Override
    public void validateNoIdenticalProductInCommerceForUpdate(UUID commerceId, UUID productId, Product current, UpdateProductRequest request) {
        if (current == null || request == null) {
            return;
        }

        // Usar request si viene presente, sino el valor actual
        String name = request.getName() != null ? request.getName().trim() : current.getName();
        var category = request.getCategory() != null ? request.getCategory() : current.getCategory();
        var expiration = request.getExpirationDate() != null ? request.getExpirationDate() : current.getExpirationDate();
        var originalPrice = request.getOriginalPrice() != null ? request.getOriginalPrice() : current.getOriginalPrice();
        var discount = request.getDiscountPercentage() != null ? request.getDiscountPercentage() : current.getDiscountPercentage();

        java.util.List<PreferenceType> preferences;
        if (request.getPreferences() != null) {
            preferences = request.getPreferences().stream().distinct().toList();
        } else {
            preferences = current.getPreferenceType() != null
                    ? current.getPreferenceType().stream().distinct().toList()
                    : java.util.List.of();
        }

        long size = preferences.size();
        boolean empty = size == 0;

        boolean exists = productRepository.existsIdenticalProductInCommerceExcludingId(
                commerceId,
                productId,
                name,
                category,
                expiration,
                originalPrice,
                discount,
                preferences,
                size,
                empty
        );

        if (exists) {
            throw new ValidationException(
                    "Ya existe otro producto idéntico registrado en este comercio (mismo nombre, categoría, preferencias, vencimiento y precio)."
            );
        }
    }
}