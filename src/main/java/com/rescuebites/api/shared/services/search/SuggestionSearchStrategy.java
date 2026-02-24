package com.rescuebites.api.shared.services.search;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.rescuebites.api.shared.utils.Constants.PRE_FILTER_PAGE_FOR_SUGGESTIONS;

/**
 * Estrategia para obtener sugerencias de búsqueda (autocomplete)
 * Jerarquía: comercios > productos por nombre > productos por descripción
 */
@Component
@RequiredArgsConstructor
public class SuggestionSearchStrategy {

    private final IProductRepository productRepository;
    private final ICommerceRepository commerceRepository;

    public List<SearchSuggestion> getSuggestionsWithPreferences(String query, List<PreferenceType> clientPreferences) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);

        // Los comercios no se filtran por preferencia
        var commerceSuggestions = getCommerceSuggestions(normalizedQuery);

        // Productos filtrados por preferencias del cliente
        var productByNameSuggestions = mapProductsToSuggestions(
                productRepository.findActiveByNameContaining(normalizedQuery, PageRequest.of(0, 5))
                        .getContent().stream()
                        .filter(p -> matchesPreferences(p, clientPreferences))
                        .toList());

        var productByDescSuggestions = mapProductsToSuggestions(
                productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, PageRequest.of(0, 5))
                        .getContent().stream()
                        .filter(p -> matchesPreferences(p, clientPreferences))
                        .toList());

        return combineSuggestions(commerceSuggestions, productByNameSuggestions, productByDescSuggestions);
    }

    public List<SearchSuggestion> getCommerceSuggestions(String normalizedQuery) {
        return commerceRepository.findActiveByNameContaining(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                .getContent().stream()
                .map(c -> new SearchSuggestion(c.getCommerceId(), c.getName(), "COMMERCE"))
                .toList();
    }

    public List<SearchSuggestion> getProductByNameSuggestions(String normalizedQuery) {
        return mapProductsToSuggestions(
                productRepository.findActiveByNameContaining(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent());
    }

    public List<SearchSuggestion> getProductByDescSuggestions(String normalizedQuery) {
        return mapProductsToSuggestions(
                productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent());
    }

    public List<SearchSuggestion> mapProductsToSuggestions(List<Product> products) {
        return products.stream()
                .map(p -> new SearchSuggestion(p.getProductId(), p.getName(), "PRODUCT"))
                .toList();
    }

    public List<SearchSuggestion> combineSuggestions(
            List<SearchSuggestion> commerceSuggestions,
            List<SearchSuggestion> productByNameSuggestions,
            List<SearchSuggestion> productByDescSuggestions) {

        List<SearchSuggestion> suggestions = new ArrayList<>(commerceSuggestions);
        suggestions.addAll(productByNameSuggestions);
        suggestions.addAll(productByDescSuggestions);
        return suggestions;
    }

    public boolean matchesPreferences(Product product, List<PreferenceType> clientPreferences) {
        if (product.getPreferenceType() == null || product.getPreferenceType().isEmpty()) {
            return false;
        }
        return product.getPreferenceType().stream()
                .anyMatch(clientPreferences::contains);
    }
}