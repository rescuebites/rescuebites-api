package com.rescuebites.api.shared.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {

    private PaginationUtils() {
    }


    /**
     * Aplica paginación manual a una lista y retorna un Page
     *
     * @param items Lista completa de elementos
     * @param pageable Información de paginación (página, tamaño)
     * @return Page con los elementos paginados
     */
    public static <T> Page<T> paginate(List<T> items, Pageable pageable) {
        if (items == null || items.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int start = (int) pageable.getOffset();
        int totalSize = items.size();
        // Si el offset supera el tamaño de la lista, retornamos una página vacía
        if (start >= totalSize) {
            return new PageImpl<>(new ArrayList<>(), pageable, totalSize);
        }
        int end = Math.min(start + pageable.getPageSize(), totalSize);
        List<T> paginatedItems = items.subList(start, end);
        return new PageImpl<>(paginatedItems, pageable, totalSize);
    }

    /**
     * Construye un Pageable para pre-fetch que trae suficientes resultados
     * desde la DB para cubrir la página solicitada por el usuario.
     * Protege contra overflow y acota el tamaño por `maxPrefetch`. Preserva el Sort.
     *
     * @param pageable    Pageable original del usuario
     * @param maxPrefetch tope máximo de elementos a prefetch
     * @return PageRequest que trae (offset + pageSize) resultados desde la página 0, acotado por maxPrefetch
     */
    public static PageRequest buildPrefetchPageable(Pageable pageable, int maxPrefetch) {
        if (maxPrefetch <= 0) {
            maxPrefetch = 100; // fallback razonable
        }

        long requiredSize = 0L;
        if (pageable != null) {
            requiredSize = pageable.getOffset() + pageable.getPageSize();
        }

        long capped = Math.min(requiredSize, maxPrefetch);
        int safeSize = (int) Math.max(1, capped);

        // preservar sort si existe
        return PageRequest.of(0, safeSize, (pageable != null) ? pageable.getSort() : Pageable.unpaged().getSort());
    }
}
