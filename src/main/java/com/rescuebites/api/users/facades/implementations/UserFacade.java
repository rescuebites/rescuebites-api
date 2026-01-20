package com.rescuebites.api.users.facades.implementations;

import com.rescuebites.api.exceptions.custom_exceptions.DuplicateResourceException;
import com.rescuebites.api.exceptions.custom_exceptions.EmailAlreadyVerifiedException;
import com.rescuebites.api.exceptions.custom_exceptions.PasswordsDoNotMatchException;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade implements IUserFacade {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void ifEmailAlreadyExistsThrowException(String email) {
        if (userRepository.existsByEmail(email)){
            throw new DuplicateResourceException("User", "email");
        }
    }

    @Override
    public void verifyIfPasswordsMatch(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)){
            throw new PasswordsDoNotMatchException();
        }
    }

    // Valido las contraseñas ingresadas para el login.
    @Override
    public void validatePasswordOrThrowException(String rawPassword, User user) {
        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new RuntimeException("Contraseña inválida.");
        }
    }

    @Override
    public void ifUserIsNotEnabledThrowException(User user) {
        if (!user.isEnabled()) {
            throw new RuntimeException("Debes confirmar tu cuenta antes de iniciar sesión");
        }
    }

    @Override
    public void ifUserIsEnabledThrowException(User user) {
        if(user.isEnabled()){
            throw new EmailAlreadyVerifiedException("El usuario ya ha sido verificado");
        }
    }
}