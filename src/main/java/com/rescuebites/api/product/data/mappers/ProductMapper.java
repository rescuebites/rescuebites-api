package com.rescuebites.api.product.data.mappers;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.commerce.data.mappers.BusinessHoursMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.utils.NormalizationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {

    public static Product toProduct(
            CreateProductRequest request,
            Commerce commerce,
            List<Image> images,
            Set<PreferenceType> preferences) {
        Product product = Product.builder()
                .commerce(commerce)
                .name(request.getName())
                .description(request.getDescription())
                .stock(request.getStock())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(request.getDiscountPercentage())
                .category(request.getCategory())
                .conditions(request.getConditions())
                .commerceType(commerce.getCommerceTypes().get(0).getName())
                .expirationDate(request.getExpirationDate())
                .preferenceType(preferences)
                .images(images)
                .active(true)
                .build();

        product.setNormalizedName(NormalizationUtils.normalizeIdentity(request.getName()));
        product.setNormalizedDescription(NormalizationUtils.normalizeIdentity(request.getDescription()));

        images.forEach(image -> image.setProduct(product));
        return product;
    }

    public static ProductResponse toProductResponse(Product product) {
        List<String> conditionDisplayNames = product.getConditions().stream()
                .map(ProductCondition::getDisplayName)
                .toList();

        return new ProductResponse(
                product.getProductId(),
                product.getCommerce().getCommerceId(),
                product.getCommerce().getName(),
                product.getCommerce().getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                BusinessHoursMapper.toBusinessHoursResponseList(product.getCommerce().getBusinessHours()),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getOriginalPrice(),
                product.getDiscountPercentage(),
                product.getDiscountedPrice(),
                product.getCategory(),
                product.getConditions(),
                conditionDisplayNames,
                product.getExpirationDate(),
                product.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                product.getActive(),
                product.getPreferenceType()
        );
    }

    public static SearchProductResponse toSearchProductResponse(Product product) {
        List<String> conditionDisplayNames = product.getConditions().stream()
                .map(ProductCondition::getDisplayName)
                .toList();

        return new SearchProductResponse(
                product.getProductId(),
                product.getCommerce().getCommerceId(),
                product.getCommerce().getName(),
                BusinessHoursMapper.toBusinessHoursResponseList(product.getCommerce().getBusinessHours()),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getOriginalPrice(),
                product.getDiscountPercentage(),
                product.getDiscountedPrice(),
                product.getCategory(),
                product.getConditions(),
                conditionDisplayNames,
                product.getExpirationDate(),
                product.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                product.getActive(),
                product.getPreferenceType()
        );
    }

    public static ProductPublicResponse toProductPublicResponse(Product product) {
        return new ProductPublicResponse(
                product.getProductId(),
                product.getName(),
                product.getStock(),
                product.getOriginalPrice(),
                product.getDiscountPercentage(),
                product.getDiscountedPrice(),
                product.getExpirationDate(),
                product.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList())
        );
    }

    public static void updateProductFromRequest(
            Product product,
            UpdateProductRequest request,
            Set<PreferenceType> preferences
            ) {
        if (request.getName() != null) {
            product.setName(request.getName());
            product.setNormalizedName(NormalizationUtils.normalizeIdentity(request.getName()));
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
            product.setNormalizedDescription(NormalizationUtils.normalizeIdentity(request.getDescription()));
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getDiscountPercentage() != null) {
            product.setDiscountPercentage(request.getDiscountPercentage());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getConditions() != null && !request.getConditions().isEmpty()) {
            product.setConditions(request.getConditions());
        }
        if (request.getExpirationDate() != null) {
            product.setExpirationDate(request.getExpirationDate());
        }

        // Agregar nuevas preferencias a las existentes (sin duplicar)
        if (preferences != null && !preferences.isEmpty()) {
            Set<PreferenceType> combined = new HashSet<>(product.getPreferenceType());
            combined.addAll(preferences);
            product.setPreferenceType(combined);
        }
    }
}