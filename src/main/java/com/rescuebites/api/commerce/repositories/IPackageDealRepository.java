package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.models.PackageDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IPackageDealRepository extends JpaRepository<PackageDeal, UUID> {

    List<PackageDeal> findByDeletedFalseAndAvailableTrue();
}
