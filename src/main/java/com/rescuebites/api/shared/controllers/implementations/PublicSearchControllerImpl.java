package com.rescuebites.api.shared.controllers.implementations;

import com.rescuebites.api.shared.controllers.interfaces.IPublicSearchController;
import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicSearchControllerImpl implements IPublicSearchController {

    private final IPublicSearchService publicSearchService;

    @Override
    public List<SearchSuggestion> getSuggestions(String query, String locality) {
        String normalizedLocality = SearchUtils.normalizeQuery(locality);
        return publicSearchService.getSuggestions(query, normalizedLocality);
    }

    @Override
    public SearchResultResponse search(String query, String locality, Pageable pageable) {
        String normalizedLocality = SearchUtils.normalizeQuery(locality);
        return publicSearchService.search(query, normalizedLocality, pageable);
    }
}
