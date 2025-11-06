package com.rescuebites.api.commerce.repositories;

import com.rescuebites.api.commerce.data.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IProductRepository extends JpaRepository<Product, UUID> {
}
