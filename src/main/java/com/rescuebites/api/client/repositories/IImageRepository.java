package com.rescuebites.api.client.repositories;

import com.rescuebites.api.data.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IImageRepository extends JpaRepository<Image, UUID> {

    void deleteByPublicId(String publicId);
}
