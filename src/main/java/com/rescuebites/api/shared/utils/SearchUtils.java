package com.rescuebites.api.shared.utils;

import java.text.Normalizer;

public class SearchUtils {

    /**
     * Normaliza un string para búsqueda:
     * - Convierte a minúsculas
     * - Elimina acentos y caracteres especiales
     * - Elimina espacios extra
     */
    public static String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return "";
        }

        String normalized = query.toLowerCase();

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        normalized = normalized.replaceAll("[^a-z0-9\\s]", "");

        normalized = normalized.trim().replaceAll("\\s+", " ");

        return normalized;
    }
}
