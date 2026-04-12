package com.rescuebites.api.users.facades.implementations;

import com.rescuebites.api.exceptions.custom_exceptions.*;
import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.repositories.IUserRepository;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserFacade implements IUserFacade {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ITokenService tokenService;

    @Override
    public void ifEmailAlreadyExistsThrowException(String email) {
        if (userRepository.existsByEmail(email)){
            throw new DuplicateResourceException("usuario", "email");
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
            throw new UnauthorizedException("El correo electrónico o la contraseña son incorrectos");
        }
    }

    @Override
    public void ifUserIsNotEnabledThrowException(User user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Debes confirmar tu cuenta antes de iniciar sesión");
        }
    }

    @Override
    public void ifUserIsEnabledThrowException(User user) {
        if(user.isEnabled()){
            throw new EmailAlreadyVerifiedException("El usuario ya ha sido verificado");
        }
    }

    @Override
    public void validateTokenNotExpired(Token token) {
        if (token.getTokenExpirationDate() == null ||
                token.getTokenExpirationDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("El token ha expirado. Solicita un nuevo enlace");
        }
    }

    @Override
    public void validateResendLimit(User user) {
        if (!tokenService.canResendToken(user)) {
            throw new TooManyRequestsException(
                    "Has superado el límite de reenvíos. Intenta nuevamente más tarde"
            );
        }
    }

    @Override
    public boolean validateAndCheckEmailChange(User user, String newEmail) {
        if (!StringUtils.hasText(newEmail)) {
            return false;
        }

        String trimmedEmail = newEmail.trim();

        if (trimmedEmail.equals(user.getEmail())) {
            return false;
        }

        ifEmailAlreadyExistsThrowException(trimmedEmail);
        return true;
    }

    @Override
    public void validatePasswordsIfProvided(String password, String confirmPassword) {
        boolean hasPassword = StringUtils.hasText(password);
        boolean hasConfirmPassword = StringUtils.hasText(confirmPassword);

        if (!hasPassword && !hasConfirmPassword) {
            return;
        }

        if (hasPassword != hasConfirmPassword) {
            throw new ValidationException("Debe ingresar y confirmar la nueva contraseña");
        }

        verifyIfPasswordsMatch(password, confirmPassword);
    }
}