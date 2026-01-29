package com.rescuebites.api.product.data.mappers;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.shared.Image;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static Product toProduct(
            CreateProductRequest request,
            Commerce commerce,
            List<Image> images,
            List<PreferenceType> preferences) {
        return Product.builder()
                .commerce(commerce)
                .name(request.getName())
                .description(request.getDescription())
                .stock(request.getStock())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(request.getDiscountPercentage())
                .category(request.getCategory())
                .condition(request.getCondition())
                .commerceType(commerce.getCommerceTypes().get(0).getName())
                .expirationDate(request.getExpirationDate())
                .preferenceType(preferences)
                .images(images)
                .active(true)
                .build();
    }

    public static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getCommerce().getCommerceId(),
                product.getCommerce().getName(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getOriginalPrice(),
                product.getDiscountPercentage(),
                product.getDiscountedPrice(),
                product.getCategory(),
                product.getCondition(),
                product.getCondition().getDisplayName(),
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
            List<PreferenceType> preferences,
            List<Image> newImages
            ) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
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
        if (request.getCondition() != null) {
            product.setCondition(request.getCondition());
        }
        if (request.getExpirationDate() != null) {
            product.setExpirationDate(request.getExpirationDate());
        }
        if (preferences != null && !preferences.isEmpty()) {
            product.setPreferenceType(preferences);
        }
        if (newImages != null && !newImages.isEmpty()) {
            product.getImages().clear();
            newImages.forEach(image -> {
                image.setProduct(product);
                product.getImages().add(image);
            });
        }
    }
}