package com.rescuebites.api.users.repositories;

import com.rescuebites.api.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    //User findByVerificationToken(String token);
}
