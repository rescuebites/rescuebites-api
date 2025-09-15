package com.rescuebites.api.client.repositories;

import com.rescuebites.api.client.data.models.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IPreferenceRepository extends JpaRepository<Preference, UUID> {

}
