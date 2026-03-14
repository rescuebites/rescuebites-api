package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.shared.controllers.responses.SearchCommerceResponse;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import com.rescuebites.api.shared.services.strategy.CommerceSearchStrategy;
import com.rescuebites.api.shared.services.strategy.ProductSearchStrategy;
import com.rescuebites.api.shared.services.strategy.SuggestionSearchStrategy;
import com.rescuebites.api.shared.utils.PaginationUtils;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicSearchServiceImpl implements IPublicSearchService {

    private final SuggestionSearchStrategy suggestionSearchStrategy;
    private final ProductSearchStrategy productSearchStrategy;
    private final CommerceSearchStrategy commerceSearchStrategy;

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String query, String normalizedLocality) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);

        var commerceSuggestions = suggestionSearchStrategy.getCommerceSuggestions(normalizedQuery, normalizedLocality);
        var productSuggestions = suggestionSearchStrategy.getProductSuggestions(normalizedQuery, normalizedLocality);

        return suggestionSearchStrategy.combineSuggestions(commerceSuggestions, productSuggestions);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResultResponse search(String query, String normalizedLocality, Pageable pageable) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);

        // 1) Si hay comercios que matchean por nombre, devolvemos comercios (y completamos por tipo).
        var commerces = commerceSearchStrategy.searchCommercesWithTypeFallback(normalizedQuery, normalizedLocality, pageable)
                .map(c -> c);
        if (!commerces.isEmpty()) {
            Page<SearchProductResponse> emptyProducts = PaginationUtils.paginate(java.util.Collections.emptyList(), pageable);
            return new SearchResultResponse(commerces, emptyProducts);
        }

        // 2) Si no hay comercios, devolvemos productos (nombre > descripción)
        var products = productSearchStrategy.searchOrderedByNameThenDescription(normalizedQuery, normalizedLocality, pageable);

        Page<SearchCommerceResponse> emptyCommerces = PaginationUtils.paginate(java.util.Collections.emptyList(), pageable);
        return new SearchResultResponse(emptyCommerces, products);
    }
}
