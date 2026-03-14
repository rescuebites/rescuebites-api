package com.rescuebites.api.shared.services.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IClientSearchService {

    List<SearchSuggestion> getSuggestions(String query, UUID clientId);

    SearchResultResponse searchWithClientPreferences(String query, UUID clientId, Pageable pageable);
}
