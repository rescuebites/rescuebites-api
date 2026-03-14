package com.rescuebites.api.shared.services.strategy;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.shared.utils.Constants.PRE_FETCH_SIZE;

/**
 * Estrategia para obtener, combinar y paginar productos de búsqueda.
 *
 * Jerarquía de resultados:
 * 1) Match exacto de la frase completa en nombre
 * 2) Match exacto de la frase completa en descripción
 * 3) Match de alguna palabra en nombre
 * 4) Match de alguna palabra en descripción
 */
@Component
@RequiredArgsConstructor
public class ProductSearchStrategy {

    private final IProductRepository productRepository;

    public Page<SearchProductResponse> searchOrderedByNameThenDescription(
            String normalizedQuery,
            String normalizedLocality,
            Pageable pageable
    ) {
        List<Product> combinedResults = searchWithWordMatching(normalizedQuery, null, normalizedLocality);
        return paginateAndMap(combinedResults, pageable);
    }

    public Page<SearchProductResponse> searchOrderedByNameThenDescriptionWithPreferences(
            String normalizedQuery,
            List<PreferenceType> clientPreferences,
            String normalizedLocality,
            Pageable pageable
    ) {
        List<Product> combinedResults = searchWithWordMatching(normalizedQuery, clientPreferences, normalizedLocality);
        return paginateAndMap(combinedResults, pageable);
    }

    private List<Product> searchWithWordMatching(String normalizedQuery, List<PreferenceType> preferences, String normalizedLocality) {
        LinkedHashMap<UUID, Product> results = new LinkedHashMap<>();
        Pageable prefetch = PageRequest.of(0, PRE_FETCH_SIZE);

        // 1) Buscar con la frase completa (prioridad máxima)
        Page<Product> exactMatches = (preferences == null)
                ? productRepository.searchProductsByQueryOrdered(normalizedQuery, prefetch)
                : productRepository.searchProductsByQueryOrderedWithPreferences(normalizedQuery, preferences, prefetch);

        for (Product p : exactMatches.getContent()) {
            if (normalizedLocality == null || normalizedLocality.isBlank() ||
                    (p.getCommerce() != null && normalizedLocality.equals(p.getCommerce().getNormalizedLocality()))) {
                results.put(p.getProductId(), p);
            }
        }

        // 2) Si la query tiene múltiples palabras, buscar también por cada palabra individual
        String[] words = normalizedQuery.trim().split("\\s+");
        if (words.length > 1) {
            for (String word : words) {
                if (word.length() < 2) continue; // Ignorar palabras muy cortas

                Page<Product> wordMatches = (preferences == null)
                        ? productRepository.searchProductsByQueryOrdered(word, prefetch)
                        : productRepository.searchProductsByQueryOrderedWithPreferences(word, preferences, prefetch);

                for (Product p : wordMatches.getContent()) {
                    if (normalizedLocality == null || normalizedLocality.isBlank() ||
                            (p.getCommerce() != null && normalizedLocality.equals(p.getCommerce().getNormalizedLocality()))) {
                        results.putIfAbsent(p.getProductId(), p);
                    }
                }
            }
        }

        return new ArrayList<>(results.values());
    }

    private Page<SearchProductResponse> paginateAndMap(List<Product> products, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), products.size());

        List<Product> pageContent = (start >= products.size())
                ? List.of()
                : products.subList(start, end);

        List<SearchProductResponse> mapped = pageContent.stream()
                .map(ProductMapper::toSearchProductResponse)
                .toList();

        return new PageImpl<>(mapped, pageable, products.size());
    }
}
