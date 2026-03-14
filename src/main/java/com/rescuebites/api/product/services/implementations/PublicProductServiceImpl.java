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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class PublicProductServiceImpl implements IPublicProductService {

    private final IProductRepository productRepository;
    private final IProductValidationFacade validationFacade;

@Override
    @Cacheable(
            value = "activeProducts",
            key = "#locality + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProducts(String locality, Pageable pageable) {
        Page<UUID> idsPage = productRepository.findAllActiveIdsByLocality(locality.trim(), pageable);
        return fetchAndMapByIds(idsPage, pageable);
    }

    @Override
    @Cacheable(
            value = "productsByCommerce",
            key = "#commerceId + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsByCommerce(UUID commerceId, Pageable pageable) {
        validationFacade.validateCommerceExists(commerceId);
        Page<UUID> idsPage = productRepository.findActiveIdsByCommerceId(commerceId, pageable);
        return fetchAndMapByIds(idsPage, pageable);
    }

    @Override
    @Cacheable(
            value = "activeProductsSortedByPrice",
            key = "#locality + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(String locality, Pageable pageable) {
        Page<UUID> idsPage = productRepository.findAllActiveIdsOrderByDiscountedPriceAscAndLocality(locality.trim(), pageable);
        return fetchAndMapByIds(idsPage, pageable);
    }

    @Override
    @Cacheable(
            value = "activeProductsByCommerceTypeSortedByPrice",
            key = "#commerceType + '-' + #locality + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort"
    )
    @Transactional(readOnly = true)
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            CommerceTypeEnum commerceType,
            String locality,
            Pageable pageable
    ) {
        Page<UUID> idsPage = productRepository.findActiveIdsByCommerceTypeAndLocalityOrderByDiscountedPriceAsc(
                commerceType, locality.trim(), pageable);

        List<ProductPublicResponse> content;
        if (idsPage.isEmpty()) {
            content = List.of();
        } else {
            var products = productRepository.findProductsWithDetailsByIds(idsPage.getContent());
            var byId = products.stream().collect(toMap(Product::getProductId, p -> p));
            content = idsPage.getContent().stream()
                    .map(byId::get)
                    .filter(java.util.Objects::nonNull)
                    .map(ProductMapper::toProductPublicResponse)
                    .toList();
        }

        return new PageImpl<>(content, pageable, idsPage.getTotalElements());
    }

    private Page<ProductResponse> fetchAndMapByIds(Page<UUID> idsPage, Pageable pageable) {
        List<ProductResponse> content;

        if (idsPage.isEmpty()) {
            content = List.of();
        } else {
            var products = productRepository.findProductsWithDetailsByIds(idsPage.getContent());
            var byId = products.stream().collect(toMap(Product::getProductId, p -> p));
            content = idsPage.getContent().stream()
                    .map(byId::get)
                    .filter(java.util.Objects::nonNull)
                    .map(ProductMapper::toProductResponse)
                    .toList();
        }

        return new PageImpl<>(content, pageable, idsPage.getTotalElements());
    }

    @Override
    @Cacheable(value = "productById", key = "#productId")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findByIdAndActive(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return ProductMapper.toProductResponse(product);
    }
}
