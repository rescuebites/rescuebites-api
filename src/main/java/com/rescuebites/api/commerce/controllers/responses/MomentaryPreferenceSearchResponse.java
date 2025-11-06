package com.rescuebites.api.commerce.controllers.responses;

import java.util.List;

public record MomentaryPreferenceSearchResponse(
        List<PackageSummaryResponse> results,
        String message
) {
}
