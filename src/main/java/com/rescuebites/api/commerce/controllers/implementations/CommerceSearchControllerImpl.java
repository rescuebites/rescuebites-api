package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.ICommerceSearchController;
import com.rescuebites.api.commerce.services.interfaces.ICommerceSearchService;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommerceSearchControllerImpl implements ICommerceSearchController {

    private final ICommerceSearchService commerceSearchService;

    @Override
    public List<SearchSuggestion> getSuggestions(UUID commerceId, String query) {
        return commerceSearchService.getSuggestions(query, commerceId);
    }

    @Override
    public Page<SearchProductResponse> search(UUID commerceId, String query, Pageable pageable) {
        return commerceSearchService.search(query, commerceId, pageable);
    }
}
