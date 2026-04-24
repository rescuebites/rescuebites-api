package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.services.interfaces.ICommerceSearchService;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import com.rescuebites.api.shared.utils.PaginationUtils;
import com.rescuebites.api.shared.utils.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.shared.utils.Constants.PRE_FETCH_SIZE;
import static com.rescuebites.api.shared.utils.Constants.PRE_FILTER_PAGE_FOR_SUGGESTIONS;

@Service
@RequiredArgsConstructor
public class CommerceSearchServiceImpl implements ICommerceSearchService {

    private final IProductRepository productRepository;
    private final ICommerceFacade commerceFacade;

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String query, UUID commerceId) {
        commerceFacade.validateCommerceOwnership(commerceId);

        String normalizedQuery = SearchUtils.normalizeQuery(query);

        List<Product> products = productRepository
                .suggestProductsByCommerceIdAndQuery(commerceId, normalizedQuery, PRE_FILTER_PAGE_FOR_SUGGESTIONS)
                .getContent();

        return products.stream()
                .map(p -> new SearchSuggestion(p.getProductId(), p.getName(), "PRODUCT"))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchProductResponse> search(String query, UUID commerceId, Pageable pageable) {
        commerceFacade.validateCommerceOwnership(commerceId);

        String normalizedQuery = SearchUtils.normalizeQuery(query);

        // El repository ya ordena por jerarquía (nombre > descripción)
        // Solo necesitamos agregar búsqueda por palabras individuales si la query tiene múltiples palabras
        List<Product> combinedResults = searchWithWordMatching(normalizedQuery, commerceId, pageable);

        Page<Product> paged = PaginationUtils.paginate(combinedResults, pageable);
        List<SearchProductResponse> mapped = paged.getContent().stream()
                .map(ProductMapper::toSearchProductResponse)
                .toList();

        return new PageImpl<>(mapped, pageable, paged.getTotalElements());
    }

    private List<Product> searchWithWordMatching(String normalizedQuery, UUID commerceId, Pageable pageable) {
        LinkedHashMap<UUID, Product> results = new LinkedHashMap<>();
        Pageable prefetch = PaginationUtils.buildPrefetchPageable(pageable, PRE_FETCH_SIZE);

        // 1) Buscar con la frase completa (prioridad máxima)
        Page<Product> exactMatches = productRepository
                .searchProductsByCommerceIdAndQuery(commerceId, normalizedQuery, prefetch);

        exactMatches.getContent().forEach(p -> results.put(p.getProductId(), p));

        // 2) Si la query tiene múltiples palabras, buscar también por cada palabra individual
        String[] words = normalizedQuery.trim().split("\\s+");
        if (words.length > 1) {
            for (String word : words) {
                if (word.length() < 2) continue; 

                Page<Product> wordMatches = productRepository
                        .searchProductsByCommerceIdAndQuery(commerceId, word, prefetch);

                wordMatches.getContent().forEach(p -> results.putIfAbsent(p.getProductId(), p));
            }
        }

        return new ArrayList<>(results.values());
    }
}
