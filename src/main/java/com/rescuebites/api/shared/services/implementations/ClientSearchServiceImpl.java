package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.controllers.responses.SearchCommerceResponse;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IClientSearchService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientSearchServiceImpl implements IClientSearchService {

    private final IPublicSearchService publicSearchService;
    private final SuggestionSearchStrategy suggestionSearchStrategy;
    private final ProductSearchStrategy productSearchStrategy;
    private final IClientFacade clientFacade;
    private final CommerceSearchStrategy commerceSearchStrategy;

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String query, UUID clientId) {
        List<PreferenceType> clientPreferences = getClientPreferences(clientId);

        String normalizedLocality = getClientNormalizedLocality(clientId);

        if (clientPreferences.isEmpty()) {
            return publicSearchService.getSuggestions(query, normalizedLocality);
        }

        return suggestionSearchStrategy.getSuggestionsFilteredByClientPreferences(query, clientPreferences, normalizedLocality);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResultResponse searchWithClientPreferences(String query, UUID clientId, Pageable pageable) {
        List<PreferenceType> clientPreferences = getClientPreferences(clientId);
        String normalizedLocality = getClientNormalizedLocality(clientId);

        if (clientPreferences.isEmpty()) {
            return publicSearchService.search(query, normalizedLocality, pageable);
        }

        String normalizedQuery = SearchUtils.normalizeQuery(query);

        // 1) Si hay comercios que matchean por nombre, devolvemos comercios (y completamos por tipo).
        var commerces = commerceSearchStrategy.searchCommercesWithTypeFallback(normalizedQuery, pageable);
        if (!commerces.isEmpty()) {
            Page<SearchProductResponse> emptyProducts = PaginationUtils.paginate(java.util.Collections.emptyList(), pageable);
            return new SearchResultResponse(commerces, emptyProducts);
        }

        // 2) Si no hay comercios, devolvemos productos filtrados por preferencias.
        var products = productSearchStrategy.searchOrderedByNameThenDescriptionWithPreferences(
                normalizedQuery,
                clientPreferences,
                normalizedLocality,
                pageable
        );

        Page<SearchCommerceResponse> emptyCommerces = PaginationUtils.paginate(java.util.Collections.emptyList(), pageable);
        return new SearchResultResponse(emptyCommerces, products);
    }

    private List<PreferenceType> getClientPreferences(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return client.getPreferences();
    }

    private String getClientNormalizedLocality(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return SearchUtils.normalizeQuery(client.getLocality().getName());
    }
}
