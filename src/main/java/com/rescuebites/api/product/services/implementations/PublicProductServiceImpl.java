package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IPublicProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    @Transactional
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllActive(pageable);
        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findByIdAndActive(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable) {

        // Validar que el comercio exista
        validationFacade.validateCommerceExists(commerceId);

        // Obtener solo productos activos del comercio
        Page<Product> products = productRepository.findActiveByCommerceId(commerceId, pageable);

        return products.map(ProductMapper::toProductResponse);
    }
}