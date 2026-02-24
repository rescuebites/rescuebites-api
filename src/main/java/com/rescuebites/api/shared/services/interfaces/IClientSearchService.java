package com.rescuebites.api.shared.services.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IClientSearchService {

    List<SearchSuggestion> getSuggestions(String query, UUID clientId);

    Page<SearchProductResponse> searchWithClientPreferences(String query, UUID clientId, Pageable pageable);
}
