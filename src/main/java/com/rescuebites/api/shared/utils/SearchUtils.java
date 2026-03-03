package com.rescuebites.api.shared.utils;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.product.data.models.Product;

import java.text.Normalizer;
import java.util.List;

public class SearchUtils {

    private SearchUtils() {
    }

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
        normalized = normalized.replaceAll("\\p{M}", "");

        normalized = normalized.replaceAll("[^a-z0-9\\s]", "");

        normalized = normalized.trim().replaceAll("\\s+", " ");

        return normalized;
    }

    /**
     * Verifica si un producto es compatible con las preferencias del cliente.
     * Productos sin preferencias definidas se consideran aptos para todos.
     */
    public static boolean matchesPreferences(Product product, List<PreferenceType> clientPreferences) {
        if (clientPreferences == null || clientPreferences.isEmpty()) {
            return true;
        }

        var productPreferences = product.getPreferenceType();
        if (productPreferences == null || productPreferences.isEmpty()) {
            return true;
        }

        return productPreferences.stream()
                .anyMatch(clientPreferences::contains);
    }
}
