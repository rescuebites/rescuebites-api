package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.commerce.controllers.requests.CreateProductRequest;
import com.rescuebites.api.commerce.data.enums.ProductCheckType;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.Product;
import com.rescuebites.api.shared.Image;

import java.util.ArrayList;
import java.util.List;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toProduct(CreateProductRequest request, Commerce commerce, List<Image> images) {
        List<ProductCheckType> checks = request.checks() != null ? new ArrayList<>(request.checks()) : new ArrayList<>();
        List<Image> productImages = images != null ? new ArrayList<>(images) : new ArrayList<>();

        return Product.builder()
                .commerce(commerce)
                .name(request.name())
                .description(request.description())
                .stock(request.stock())
                .originalPrice(request.originalPrice())
                .discountPercentage(request.discountPercentage())
                .expirationDate(request.expirationDate())
                .checks(checks)
                .images(productImages)
                .build();
    }
}
