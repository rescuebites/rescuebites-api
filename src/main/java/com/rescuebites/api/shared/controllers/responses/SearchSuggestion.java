package com.rescuebites.api.shared.controllers.responses;

import java.util.UUID;

public record SearchSuggestion(
        UUID id,
        String label,
        String type  // "PRODUCT" o "COMMERCE"
) {}
