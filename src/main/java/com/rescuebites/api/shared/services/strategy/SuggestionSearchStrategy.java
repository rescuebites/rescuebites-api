package com.rescuebites.api.shared.services.strategy;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rescuebites.api.shared.utils.Constants.PRE_FILTER_PAGE_FOR_SUGGESTIONS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Estrategia para obtener sugerencias de búsqueda (autocomplete)
 * Jerarquía: comercios > productos por nombre > productos por descripción
 */
@Component
@RequiredArgsConstructor
public class SuggestionSearchStrategy {

    private final IProductRepository productRepository;
    private final ICommerceRepository commerceRepository;

    public List<SearchSuggestion> getCommerceSuggestions(String normalizedQuery, String normalizedLocality) {
        List<com.rescuebites.api.commerce.data.models.Commerce> commerces;
        if (normalizedLocality == null || normalizedLocality.isBlank()) {
            commerces = commerceRepository.findActiveByNameContainingRanked(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                    .getContent();
        } else {
            commerces = commerceRepository.findActiveByNameContainingRankedAndLocality(normalizedQuery, normalizedLocality, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                    .getContent();
        }

        // Detectar nombres duplicados dentro del set acotado de sugerencias
        var duplicatedNames = commerces.stream()
                .collect(groupingBy(c -> c.getName() == null ? "" : c.getName().trim(), counting()));

        return commerces.stream()
                .map(c -> {
                    String name = c.getName();
                    boolean isDuplicated = duplicatedNames.getOrDefault(name == null ? "" : name.trim(), 0L) > 1;

                    String label = name;
                    if (isDuplicated) {
                        String addressPart = (c.getAddress() != null && !c.getAddress().isBlank()) ? c.getAddress().trim() : "";
                        String localityPart = (c.getLocality() != null && c.getLocality().getName() != null && !c.getLocality().getName().isBlank()) ? c.getLocality().getName().trim() : "";
                        String suffix = (addressPart + (localityPart.isBlank() ? "" : ", " + localityPart)).trim();
                        if (!suffix.isBlank()) {
                            label = name + " — " + suffix;
                        }
                    }
                    return new SearchSuggestion(c.getCommerceId(), label, "COMMERCE");
                })
                .toList();
    }

    public List<SearchSuggestion> getProductSuggestions(String normalizedQuery, String normalizedLocality) {
        List<Product> products;
        if (normalizedLocality == null || normalizedLocality.isBlank()) {
            products = productRepository.suggestProductsHierarchy(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent();
        } else {
            products = productRepository.suggestProductsHierarchyAndLocality(normalizedQuery, normalizedLocality, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent();
        }

        return mapProductsToSuggestions(products);
    }

    // Nuevo: sugerencias de producto filtradas por preferencias (y por localidad cuando aplique)
    public List<SearchSuggestion> getProductSuggestions(String normalizedQuery, String normalizedLocality, List<PreferenceType> preferences) {
        List<Product> products;
        if (normalizedLocality == null || normalizedLocality.isBlank()) {
            products = productRepository.suggestProductsHierarchyWithPreferences(normalizedQuery, preferences, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent();
        } else {
            products = productRepository.suggestProductsHierarchyWithPreferencesAndLocality(normalizedQuery, preferences, normalizedLocality, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent();
        }

        return mapProductsToSuggestions(products);
    }

    public List<SearchSuggestion> mapProductsToSuggestions(List<Product> products) {
        return products.stream()
                .map(p -> new SearchSuggestion(p.getProductId(), p.getName(), "PRODUCT"))
                .toList();
    }

    public List<SearchSuggestion> combineSuggestions(
            List<SearchSuggestion> commerceSuggestions,
            List<SearchSuggestion> productSuggestions) {

        List<SearchSuggestion> suggestions = new ArrayList<>(commerceSuggestions);
        suggestions.addAll(productSuggestions);
        return suggestions;
    }
}