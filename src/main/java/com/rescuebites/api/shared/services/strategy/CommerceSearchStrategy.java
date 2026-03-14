package com.rescuebites.api.shared.services.strategy;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.shared.controllers.responses.SearchCommerceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rescuebites.api.shared.utils.Constants.DEFAULT_EXTRA_BY_TYPE_LIMIT;

/**
 * Estrategia para búsqueda de comercios.
 * Reglas:
 * 1) devolver primero los comercios que matchean por nombre;
 * 2) si queda "vacío", completar con comercios del mismo tipo que los matcheados.
 */
@Component
@RequiredArgsConstructor
public class CommerceSearchStrategy {

    private final ICommerceRepository commerceRepository;

    public Page<SearchCommerceResponse> searchCommercesWithTypeFallback(String normalizedQuery, Pageable pageable) {
        Page<Commerce> matched = commerceRepository.findActiveByNameContainingRanked(normalizedQuery, pageable);

        if (matched.isEmpty()) {
            return matched.map(CommerceMapper::toSearchCommerceResponse);
        }

        // Mapear manteniendo el orden de los resultados rankeados
        LinkedHashMap<UUID, Commerce> ordered = new LinkedHashMap<>();
        for (Commerce c : matched.getContent()) {
            ordered.put(c.getCommerceId(), c);
        }

        // Tipos de los comercios matcheados
        Set<CommerceTypeEnum> matchedTypes = matched.getContent().stream()
                .flatMap(c -> c.getCommerceTypes() == null ? java.util.stream.Stream.empty() : c.getCommerceTypes().stream())
                .map(CommerceType::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!matchedTypes.isEmpty()) {
            int remaining = Math.max(0, pageable.getPageSize() - ordered.size());
            int extraSize = remaining > 0 ? remaining : DEFAULT_EXTRA_BY_TYPE_LIMIT;

            Page<Commerce> extras;
            if (ordered.isEmpty()) {
                extras = commerceRepository.findActiveByCommerceTypes(
                        matchedTypes,
                        PageRequest.of(0, extraSize)
                );
            } else {
                extras = commerceRepository.findActiveByCommerceTypesExcludingIds(
                        matchedTypes,
                        ordered.keySet(),
                        PageRequest.of(0, extraSize)
                );
            }

            for (Commerce c : extras.getContent()) {
                ordered.putIfAbsent(c.getCommerceId(), c);
            }
        }

        List<SearchCommerceResponse> content = ordered.values().stream()
                .map(CommerceMapper::toSearchCommerceResponse)
                .toList();

        // TotalElements: mínimo el total real de matches; extras son "relleno".
        long total = Math.max(matched.getTotalElements(), content.size());
        return new PageImpl<>(content, pageable, total);
    }
}
