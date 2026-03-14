package com.rescuebites.api.product.services.interfaces;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPublicProductService {

    Page<ProductResponse> getAllActiveProducts(String locality, Pageable pageable);

    ProductResponse getProductById(UUID productId);

    Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable);

    Page<ProductResponse> getAllActiveProductsOrderedByPrice(String locality, Pageable pageable);

    Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            CommerceTypeEnum commerceType,
            String locality,
            Pageable pageable
    );
}
