package com.rescuebites.api.shared.controllers.implementations;

import com.rescuebites.api.shared.controllers.interfaces.IClientSearchController;
import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IClientSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientSearchControllerImpl implements IClientSearchController {

    private final IClientSearchService clientSearchService;

    @Override
    public List<SearchSuggestion> getSuggestions(UUID clientId, String query) {
        return clientSearchService.getSuggestions(query, clientId);
    }

    @Override
    public SearchResultResponse searchWithPreferences(UUID clientId, String query, Pageable pageable) {
        return clientSearchService.searchWithClientPreferences(query, clientId, pageable);
    }
}
