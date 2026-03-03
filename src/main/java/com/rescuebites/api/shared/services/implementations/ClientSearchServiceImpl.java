package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IClientSearchService;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import com.rescuebites.api.shared.services.strategy.ProductSearchStrategy;
import com.rescuebites.api.shared.services.strategy.SuggestionSearchStrategy;
import com.rescuebites.api.shared.utils.SearchUtils;
import com.rescuebites.api.security.utils.SecurityUtils;
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

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String query, UUID clientId) {
        List<PreferenceType> clientPreferences = getClientPreferences(clientId);

        if (clientPreferences.isEmpty()) {
            return publicSearchService.getSuggestions(query);
        }

        return suggestionSearchStrategy.getSuggestionsFilteredByClientPreferences(query, clientPreferences);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchProductResponse> searchWithClientPreferences(String query, UUID clientId, Pageable pageable) {
        List<PreferenceType> clientPreferences = getClientPreferences(clientId);

        if (clientPreferences.isEmpty()) {
            return publicSearchService.search(query, pageable);
        }

        String normalizedQuery = SearchUtils.normalizeQuery(query);
        return productSearchStrategy.searchAndPaginateWithPreferences(normalizedQuery, clientPreferences, pageable);
    }

    private List<PreferenceType> getClientPreferences(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return client.getPreferences() != null
                ? client.getPreferences()
                : List.of();
    }
}
