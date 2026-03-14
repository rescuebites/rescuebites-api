package com.rescuebites.api.shared.utils;

public class SearchUtils {

    private SearchUtils() {
    }

    /**
     * Normaliza un string para búsqueda.
     *
     * Nota: delega en {@link NormalizationUtils#normalizeIdentity(String)} para mantener
     * consistencia entre sugerencias/búsqueda y validaciones de identidad.
     */
    public static String normalizeQuery(String query) {
        String normalized = NormalizationUtils.normalizeIdentity(query);
        return normalized == null ? "" : normalized;
    }
}
