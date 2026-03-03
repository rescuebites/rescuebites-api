package com.rescuebites.api.shared.services.strategy;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.shared.utils.PaginationUtils;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Estrategia para obtener, combinar y paginar productos de búsqueda
 * Jerarquía: productos del comercio > por nombre > por descripción
 */
@Component
@RequiredArgsConstructor
public class ProductSearchStrategy {

    private final IProductRepository productRepository;
    private final ICommerceRepository commerceRepository;

    public Page<SearchProductResponse> searchAndPaginate(String normalizedQuery, Pageable pageable) {
        var prefetchPage = PaginationUtils.buildPrefetchPageable(pageable);

        var productsByCommerce = productRepository.findActiveByCommerceName(normalizedQuery, prefetchPage).getContent();
        var productsByName = productRepository.findActiveByNameContaining(normalizedQuery, prefetchPage).getContent();
        var productsByDesc = productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, prefetchPage).getContent();

        List<Product> combined = combineWithHierarchy(productsByCommerce, productsByName, productsByDesc);
        return PaginationUtils.paginate(ProductMapper.toProductResponseList(combined), pageable);
    }

    public Page<SearchProductResponse> searchAndPaginateWithPreferences(
            String normalizedQuery, List<PreferenceType> clientPreferences, Pageable pageable) {

        var prefetchPage = PaginationUtils.buildPrefetchPageable(pageable);

        var productsByCommerce = getProductsByCommerceWithPreferences(normalizedQuery, clientPreferences, prefetchPage);
        var productsByName = getProductsByNameWithPreferences(normalizedQuery, clientPreferences, prefetchPage);
        var productsByDesc = getProductsByDescriptionWithPreferences(normalizedQuery, clientPreferences, prefetchPage);

        List<Product> combined = combineWithHierarchy(productsByCommerce, productsByName, productsByDesc);
        return PaginationUtils.paginate(ProductMapper.toProductResponseList(combined), pageable);
    }

    private List<Product> getProductsByCommerceWithPreferences(
            String normalizedQuery, List<PreferenceType> clientPreferences, Pageable prefetchPage) {

        List<UUID> commerceIds = commerceRepository.findActiveByNameContaining(normalizedQuery, prefetchPage)
                .getContent().stream()
                .map(Commerce::getCommerceId)
                .toList();

        if (commerceIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findActiveByCommerceIdsWithPreferences(commerceIds, clientPreferences);
    }

    private List<Product> getProductsByNameWithPreferences(
            String normalizedQuery, List<PreferenceType> clientPreferences, Pageable prefetchPage) {

        return productRepository.findActiveByNameContaining(normalizedQuery, prefetchPage)
                .getContent().stream()
                .filter(p -> SearchUtils.matchesPreferences(p, clientPreferences))
                .toList();
    }

    private List<Product> getProductsByDescriptionWithPreferences(
            String normalizedQuery, List<PreferenceType> clientPreferences, Pageable prefetchPage) {

        return productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, prefetchPage)
                .getContent().stream()
                .filter(p -> SearchUtils.matchesPreferences(p, clientPreferences))
                .toList();
    }

    private List<Product> combineWithHierarchy(
            List<Product> productsByCommerce,
            List<Product> productsByName,
            List<Product> productsByDesc) {

        Set<Product> combined = new LinkedHashSet<>();
        combined.addAll(productsByCommerce);
        combined.addAll(productsByName);
        combined.addAll(productsByDesc);
        return new ArrayList<>(combined);
    }
}