package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.services.interfaces.IClientSearchService;
import com.rescuebites.api.shared.services.interfaces.IPublicSearchService;
import com.rescuebites.api.shared.services.search.ProductSearchStrategy;
import com.rescuebites.api.shared.services.search.SuggestionSearchStrategy;
import com.rescuebites.api.shared.utils.PaginationUtils;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.shared.utils.Constants.PRE_FILTER_PAGE_FOR_SEARCH;

@Service
@RequiredArgsConstructor
public class ClientSearchServiceImpl implements IClientSearchService {

    private final IProductRepository productRepository;
    private final ICommerceRepository commerceRepository;
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
        List<Product> combined = productSearchStrategy.combineWithHierarchy(
                getProductsByCommerce(normalizedQuery, clientPreferences),
                getProductsByName(normalizedQuery, clientPreferences),
                getProductsByDescription(normalizedQuery, clientPreferences)
        );

        return PaginationUtils.paginate(ProductMapper.toProductResponseList(combined), pageable);
    }

    private List<PreferenceType> getClientPreferences(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return client.getPreferences() != null
                ? client.getPreferences()
                : List.of();
    }

    private List<Product> getProductsByCommerce(String normalizedQuery, List<PreferenceType> clientPreferences) {
        List<UUID> commerceIds = commerceRepository.findActiveByNameContaining(normalizedQuery, PRE_FILTER_PAGE_FOR_SEARCH)
                .getContent().stream()
                .map(Commerce::getCommerceId)
                .toList();

        if (commerceIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findActiveByCommerceIdsWithPreferences(commerceIds, clientPreferences);
    }

    private List<Product> getProductsByName(String normalizedQuery, List<PreferenceType> clientPreferences) {
        return productRepository.findActiveByNameContaining(normalizedQuery, PRE_FILTER_PAGE_FOR_SEARCH)
                .getContent().stream()
                .filter(p -> SearchUtils.matchesPreferences(p, clientPreferences))
                .toList();
    }

    private List<Product> getProductsByDescription(String normalizedQuery, List<PreferenceType> clientPreferences) {
        return productRepository.findActiveByDescriptionContainingExcludingName(normalizedQuery, PRE_FILTER_PAGE_FOR_SEARCH)
                .getContent().stream()
                .filter(p -> SearchUtils.matchesPreferences(p, clientPreferences))
                .toList();
    }
}
