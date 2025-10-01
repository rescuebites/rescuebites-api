package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.models.CommerceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ICommerceTypeRepository extends JpaRepository<CommerceType, UUID> {

}