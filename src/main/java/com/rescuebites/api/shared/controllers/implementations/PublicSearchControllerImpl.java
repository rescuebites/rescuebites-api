package com.rescuebites.api.shared.controllers.implementations;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.interfaces.IPublicSearchController;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicSearchControllerImpl implements IPublicSearchController {

    private final IPublicSearchService publicSearchService;

    @Override
    public List<SearchSuggestion> getSuggestions(String query) {
        return publicSearchService.getSuggestions(query);
    }

    @Override
    public Page<SearchProductResponse> search(String query, Pageable pageable) {
        return publicSearchService.search(query, pageable);
    }
}
