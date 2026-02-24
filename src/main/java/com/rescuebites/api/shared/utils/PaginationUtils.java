package com.rescuebites.api.shared.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        int end = Math.min(start + pageable.getPageSize(), items.size());
        List<T> paginatedItems = items.subList(start, Math.max(start, end));

        return new PageImpl<>(paginatedItems, pageable, items.size());
    }
}
