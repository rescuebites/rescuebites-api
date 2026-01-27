package com.rescuebites.api.product.controllers.implementations;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.controllers.interfaces.IPublicProductController;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.services.interfaces.IPublicProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PublicProductController implements IPublicProductController {

    private final IPublicProductService publicProductService;

    @Override
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return publicProductService.getAllActiveProducts(pageable);
    }

    @Override
    public ProductResponse getProductById(UUID productId) {
        return publicProductService.getProductById(productId);
    }

    @Override
    public Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable) {
        return publicProductService.getActiveProductsByCommerce(commerceId, pageable);
    }

    @Override
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(Pageable pageable) {
        return publicProductService.getAllActiveProductsOrderedByPrice(pageable);
    }

    @Override
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            CommerceTypeEnum commerceType,
            Pageable pageable
    ) {
        return publicProductService.getActiveProductsByCommerceTypeOrderedByPrice(commerceType, pageable);
    }
}