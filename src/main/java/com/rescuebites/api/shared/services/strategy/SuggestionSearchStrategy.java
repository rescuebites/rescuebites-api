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

    public List<SearchSuggestion> getSuggestionsFilteredByClientPreferences(String query, List<PreferenceType> clientPreferences, String normalizedLocality) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);

        // Comercios (filtrados por localidad si existe)
        var commerceSuggestions = getCommerceSuggestions(normalizedQuery, normalizedLocality);

        // Productos (filtrados en DB por preferencias y por localidad cuando aplique)
        var products = productRepository
                .suggestProductsHierarchyWithPreferences(normalizedQuery, clientPreferences, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                .getContent();

        // Filtrar por localidad en memoria (page pequeña)
        if (normalizedLocality != null && !normalizedLocality.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getCommerce() != null && normalizedLocality.equals(p.getCommerce().getNormalizedLocality()))
                    .toList();
        }

        var productSuggestions = mapProductsToSuggestions(products);

        return combineSuggestions(commerceSuggestions, productSuggestions);
    }

    public List<SearchSuggestion> getCommerceSuggestions(String normalizedQuery, String normalizedLocality) {
        var commerces = commerceRepository.findActiveByNameContainingRanked(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                .getContent();

        commerces = commerces.stream()
                .filter(c -> c.getNormalizedLocality() != null && normalizedLocality.equals(c.getNormalizedLocality()))
                .toList();

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
        var products = productRepository.suggestProductsHierarchy(normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS).getContent();

        if (normalizedLocality != null && !normalizedLocality.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getCommerce() != null && normalizedLocality.equals(p.getCommerce().getNormalizedLocality()))
                    .toList();
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