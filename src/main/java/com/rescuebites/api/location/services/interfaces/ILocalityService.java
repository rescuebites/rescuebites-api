package com.rescuebites.api.location.services.interfaces;

import com.rescuebites.api.location.data.models.Locality;

import java.util.Optional;

public interface ILocalityService {
    Locality resolveOrCreateByName(String rawName);
    //Optional<Locality> resolveOrCreateByName(String rawName);
    Optional<Locality> findByNormalizedName(String normalized);
}
