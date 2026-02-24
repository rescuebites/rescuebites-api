package com.rescuebites.api.shared.services.search;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.shared.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Estrategia para obtener, combinar y paginar productos de búsqueda
 * Jerarquía: productos del comercio > por nombre > por descripción
 */
@Component
@RequiredArgsConstructor
public class ProductSearchStrategy {

    private final IProductRepository productRepository;

    public Page<SearchProductResponse> searchAndPaginate(String normalizedQuery, Pageable pageable) {
        var productsByCommerce = productRepository.findActiveByCommerceName(normalizedQuery, pageable).getContent();
        var productsByName = productRepository.findActiveByNameContaining(normalizedQuery, pageable).getContent();
        var productsByDesc = productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, pageable).getContent();

        List<Product> combined = combineWithHierarchy(productsByCommerce, productsByName, productsByDesc);
        return PaginationUtils.paginate(ProductMapper.toProductResponseList(combined), pageable);
    }

    public List<Product> combineWithHierarchy(
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