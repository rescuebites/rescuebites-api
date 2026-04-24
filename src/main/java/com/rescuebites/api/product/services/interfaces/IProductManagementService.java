package com.rescuebites.api.product.services.interfaces;

import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.enums.ExpirationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IProductManagementService {

    void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images);

    Page<ProductResponse> getProductsByCommerce(UUID commerceId, Pageable pageable);

    Page<ProductResponse> getProductsByCommerceOrderedByStock(UUID commerceId, Pageable pageable);

    Page<ProductResponse> getProductsByExpirationFilter(UUID commerceId, ExpirationFilter filter, Pageable pageable);

    ProductResponse getProductByCommerceAndId(UUID commerceId, UUID productId);

    void updateProduct(UUID commerceId, UUID productId, UpdateProductRequest request, MultipartFile[] images);

    void activateProduct(UUID commerceId, UUID productId);

    void deactivateProduct(UUID commerceId, UUID productId);
}