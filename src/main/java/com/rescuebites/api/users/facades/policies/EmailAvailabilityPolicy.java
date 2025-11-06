package com.rescuebites.api.users.facades.policies;

import com.rescuebites.api.exceptions.custom_exceptions.DuplicateResourceException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.users.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EmailAvailabilityPolicy {

    private final IUserRepository userRepository;

    public void ensureAvailable(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("El email es obligatorio");
        }

        String trimmedEmail = email.trim();

        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new DuplicateResourceException("User", "email");
        }
    }
}
