package com.rescuebites.api.product.services.interfaces;

import com.rescuebites.api.product.controllers.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPublicProductService {

    Page<ProductResponse> getAllActiveProducts(Pageable pageable);

    ProductResponse getProductById(UUID productId);

    // ⭐ NUEVO: Productos activos de un comercio específico
    Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable);
}
