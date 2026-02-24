package com.rescuebites.api.shared.services.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPublicSearchService {

    List<SearchSuggestion> getSuggestions(String query);

    Page<SearchProductResponse> search(String query, Pageable pageable);
}
