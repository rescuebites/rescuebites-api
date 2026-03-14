package com.rescuebites.api.shared.services.strategy;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.shared.controllers.responses.SearchCommerceResponse;
import com.rescuebites.api.shared.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Page<SearchCommerceResponse> searchCommercesWithTypeFallback(String normalizedQuery, String normalizedLocality, Pageable pageable) {
        Page<Commerce> matched = commerceRepository.findActiveByNameContainingRankedAndLocality(normalizedQuery, normalizedLocality, pageable);

        if (matched.isEmpty()) {
            return matched.map(CommerceMapper::toSearchCommerceResponse);
        }

        LinkedHashMap<UUID, Commerce> ordered = new LinkedHashMap<>();
        for (Commerce c : matched.getContent()) {
            ordered.put(c.getCommerceId(), c);
        }

        Set<CommerceTypeEnum> matchedTypes = matched.getContent().stream()
                .flatMap(c -> c.getCommerceTypes() == null ? Stream.empty() : c.getCommerceTypes().stream())
                .map(CommerceType::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!matchedTypes.isEmpty()) {
            int remaining = Math.max(0, pageable.getPageSize() - ordered.size());
            // Sólo pedir extras si queda espacio en la página solicitada
            if (remaining > 0) {
                Page<Commerce> extras;
            if (ordered.isEmpty()) {
                extras = commerceRepository.findActiveByCommerceTypesAndLocality(
                        matchedTypes,
                        normalizedLocality,
                        PageRequest.of(0, remaining)
                );
            } else {
                extras = commerceRepository.findActiveByCommerceTypesExcludingIdsAndLocality(
                        matchedTypes,
                        ordered.keySet(),
                        normalizedLocality,
                        PageRequest.of(0, remaining)
                );
            }
                for (Commerce c : extras.getContent()) {
                    ordered.putIfAbsent(c.getCommerceId(), c);
                }
            }
        }

        // Convertir ordered a lista y paginar de forma consistente
        List<Commerce> orderedList = new ArrayList<>(ordered.values());
        Page<Commerce> paged = PaginationUtils.paginate(orderedList, pageable);

        // Mapear sólo el contenido paginado
        List<SearchCommerceResponse> mapped = paged.getContent().stream()
                .map(CommerceMapper::toSearchCommerceResponse)
                .toList();

        // Mantener total real: el mayor entre matched (BD) y el total de la lista combinada
        long total = Math.max(matched.getTotalElements(), paged.getTotalElements());
        return new PageImpl<>(mapped, pageable, total);
    }
}
