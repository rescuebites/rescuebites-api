package com.rescuebites.api.shared.services.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPublicSearchService {

    List<SearchSuggestion> getSuggestions(String query, String normalizedLocality);

    SearchResultResponse search(String query, String normalizedLocality, Pageable pageable);
}
