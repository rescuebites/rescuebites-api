package com.rescuebites.api.location.services.implementations;

import com.rescuebites.api.location.data.models.Locality;
import com.rescuebites.api.location.mappers.LocalityMapper;
import com.rescuebites.api.location.repositories.ILocalityRepository;
import com.rescuebites.api.location.services.interfaces.ILocalityService;
import com.rescuebites.api.shared.utils.NormalizationUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LocalityService implements ILocalityService {

    private final ILocalityRepository localityRepository;

    public LocalityService(ILocalityRepository localityRepository) {
        this.localityRepository = localityRepository;
    }

    @Cacheable(cacheNames = "localities", key = "#normalized")
    public Optional<Locality> findByNormalizedName(String normalized) {
        return localityRepository.findByNormalizedName(normalized);
    }

    public Locality save(Locality locality) {
        return localityRepository.save(locality);
    }

    @Transactional
    public Optional<Locality> resolveOrCreateByName(String rawName) {

        if (rawName == null || rawName.isBlank()) {
            return Optional.empty();
        }

        String normalized = NormalizationUtils.normalizeIdentity(rawName);

        Optional<Locality> existing = findByNormalizedName(normalized);
        if (existing.isPresent()) {
            return existing;
        }

        Locality locality = LocalityMapper.toLocality(rawName, normalized);

        try {
            Locality saved = localityRepository.save(locality);
            return Optional.of(saved);
        } catch (DataIntegrityViolationException ex) {
            return localityRepository.findByNormalizedName(normalized);
        }
    }
}
