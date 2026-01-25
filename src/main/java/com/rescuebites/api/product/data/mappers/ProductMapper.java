package com.rescuebites.api.product.data.mappers;

import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.shared.Image;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static Product toProduct(CreateProductRequest request, Commerce commerce, List<Image> images) {
        return Product.builder()
                .commerce(commerce)
                .name(request.name())
                .description(request.description())
                .stock(request.stock())
                .originalPrice(request.originalPrice())
                .discountPercentage(request.discountPercentage())
                .category(request.category())
                .condition(request.condition())
                .commerceType(commerce.getCommerceTypes().get(0).getName())
                .expirationDate(request.expirationDate())
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
                product.getExpirationDate(),
                product.getImages().stream()
                        .map(Image::getUrl)
                        .collect(Collectors.toList()),
                product.getActive()
        );
    }
}