package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IPublicProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio público de consulta de productos para clientes
 */
@Service
@RequiredArgsConstructor
public class PublicProductServiceImpl implements IPublicProductService {

    private final IProductRepository productRepository;
    private final IProductValidationFacade validationFacade;

    @Override
    @Cacheable(
            value = "activeProducts",
            key = "'all-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize"
    )
    @Transactional
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllActive(pageable);
        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Cacheable(value = "productById", key = "#productId")
    @Transactional
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findByIdAndActive(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Cacheable(
            value = "productsByCommerce",
            key = "#commerceId + '-page-' + #pageable.pageNumber"
    )
    @Transactional
    public Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable) {

        validationFacade.validateCommerceExists(commerceId);
        Page<Product> products = productRepository.findActiveByCommerceId(commerceId, pageable);

        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Cacheable(
            value = "activeProductsSortedByPrice",
            key = "'all-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize"
    )
    @Transactional
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(Pageable pageable) {
        Page<Product> products = productRepository.findAllActiveOrderByDiscountedPriceAsc(pageable);
        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Cacheable(
            value = "activeProductsByCommerceTypeSortedByPrice",
            key = "#commerceType + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize"
    )
    @Transactional
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            CommerceTypeEnum commerceType,
            Pageable pageable
    ) {
        Page<Product> products = productRepository
                .findActiveByCommerceTypeOrderByDiscountedPriceAsc(commerceType, pageable);
        return products.map(ProductMapper::toProductPublicResponse);
    }
}