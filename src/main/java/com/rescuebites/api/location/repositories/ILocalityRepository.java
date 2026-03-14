package com.rescuebites.api.location.repositories;

import com.rescuebites.api.location.data.models.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ILocalityRepository extends JpaRepository<Locality, UUID> {
    Optional<Locality> findByNormalizedName(String normalizedName);
}
