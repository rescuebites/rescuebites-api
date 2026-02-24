package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import com.rescuebites.api.shared.services.search.ProductSearchStrategy;
import com.rescuebites.api.shared.services.search.SuggestionSearchStrategy;
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

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String query) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);

        var commerceSuggestions = suggestionSearchStrategy.getCommerceSuggestions(normalizedQuery);
        var productByNameSuggestions = suggestionSearchStrategy.getProductByNameSuggestions(normalizedQuery);
        var productByDescSuggestions = suggestionSearchStrategy.getProductByDescSuggestions(normalizedQuery);

        return suggestionSearchStrategy.combineSuggestions(commerceSuggestions, productByNameSuggestions, productByDescSuggestions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchProductResponse> search(String query, Pageable pageable) {
        String normalizedQuery = SearchUtils.normalizeQuery(query);
        return productSearchStrategy.searchAndPaginate(normalizedQuery, pageable);
    }
}
