package com.rescuebites.api.product.controllers.implementations;

import com.rescuebites.api.product.controllers.interfaces.IProductManagementController;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.enums.ExpirationFilter;
import com.rescuebites.api.product.services.interfaces.IProductManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductManagementControllerImpl implements IProductManagementController {

    private final IProductManagementService productManagementService;

    @Override
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {
        productManagementService.createProduct(commerceId, request, images);
    }

    @Override
    public Page<ProductResponse> getProductsByCommerce(UUID commerceId, Pageable pageable) {
        return productManagementService.getProductsByCommerce(commerceId, pageable);
    }

    @Override
    public ProductResponse getProduct(UUID commerceId, UUID productId) {
        return productManagementService.getProductByCommerceAndId(commerceId, productId);
    }

    @Override
    public void updateProduct(UUID commerceId, UUID productId, UpdateProductRequest request, MultipartFile[] images) {
        productManagementService.updateProduct(commerceId, productId, request, images);
    }

    @Override
    public void activateProduct(UUID commerceId, UUID productId) {
        productManagementService.activateProduct(commerceId, productId);
    }

    @Override
    public void deactivateProduct(UUID commerceId, UUID productId) {
        productManagementService.deactivateProduct(commerceId, productId);
    }

    @Override
    public Page<ProductResponse> getProductsByCommerceOrderedByStock(UUID commerceId, Pageable pageable) {
        return productManagementService.getProductsByCommerceOrderedByStock(commerceId, pageable);
    }

    @Override
    public Page<ProductResponse> getProductsByExpirationFilter(UUID commerceId, ExpirationFilter filter, Pageable pageable) {
        return productManagementService.getProductsByExpirationFilter(commerceId, filter, pageable);
    }
}
