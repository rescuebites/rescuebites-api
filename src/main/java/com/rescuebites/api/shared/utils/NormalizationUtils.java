package com.rescuebites.api.shared.utils;

import java.text.Normalizer;

/**
 * Utilidades de normalización para comparaciones de identidad (unicidad) y búsqueda.
 * <p>
 * Regla:
 * - trim
 * - lowercase
 * - remover acentos/diacríticos
 * - colapsar espacios internos múltiples
 */
public final class NormalizationUtils {

    private NormalizationUtils() {
    }

    public static String normalizeIdentity(String input) {
        if (input == null) {
            return null;
        }

        String normalized = input.trim().toLowerCase();
        if (normalized.isBlank()) {
            return "";
        }

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Mantener letras/números/espacios y signos comunes en direcciones
        normalized = normalized.replaceAll("[^a-z0-9\\s#./-]", "");

        normalized = normalized.replaceAll("\\s+", " ");
        normalized = normalized.trim();

        return normalized.trim();
    }
}
