package com.rescuebites.api.shared.controllers.responses;

import org.springframework.data.domain.Page;

public record SearchResultResponse(
        Page<SearchCommerceResponse> commerces,
        Page<SearchProductResponse> products
) {}
