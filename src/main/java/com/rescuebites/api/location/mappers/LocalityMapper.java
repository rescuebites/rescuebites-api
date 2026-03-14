package com.rescuebites.api.location.mappers;

import com.rescuebites.api.location.data.models.Locality;

import java.util.UUID;

public final class LocalityMapper {

    private LocalityMapper() {
    }

    public static Locality toLocality(String rawName, String normalized) {
        return Locality.builder()
                .localityId(UUID.randomUUID())
                .name(rawName.trim())
                .normalizedName(normalized)
                .build();
    }
}
