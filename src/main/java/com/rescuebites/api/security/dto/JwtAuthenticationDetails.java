package com.rescuebites.api.security.dto;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.UUID;

public record JwtAuthenticationDetails(
        WebAuthenticationDetails webDetails,
        UUID commerceId,
        String commerceType,
        UUID clientId
) {}
