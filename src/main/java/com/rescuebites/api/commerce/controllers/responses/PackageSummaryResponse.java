package com.rescuebites.api.commerce.controllers.responses;

import java.util.List;
import java.util.UUID;

public record PackageSummaryResponse(
        UUID packageId,
        String name,
        String description,
        UUID commerceId,
        List<String> applicablePreferences
) {
}
