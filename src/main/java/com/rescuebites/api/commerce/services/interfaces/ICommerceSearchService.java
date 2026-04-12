package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICommerceSearchService {

    List<SearchSuggestion> getSuggestions(String query, UUID commerceId);

    Page<SearchProductResponse> search(String query, UUID commerceId, Pageable pageable);
}
