package com.rescuebites.api.commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rescuebites.api.commerce.data.models.Commerce;
import java.util.UUID;

public interface ICommerceRepository extends JpaRepository<Commerce, UUID>{
}
