package com.rescuebites.api.product.services.interfaces;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IClientProductService {

    Page<ProductResponse> getProductsMatchingClientPreferences(UUID clientId, Pageable pageable);

    Page<ProductResponse> getActiveProductsByCommerce(UUID clientId, UUID commerceId, Pageable pageable);

    Page<ProductResponse> getAllActiveProductsOrderedByPrice(UUID clientId, Pageable pageable);

    Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(UUID clientId, CommerceTypeEnum commerceType, Pageable pageable);
}
