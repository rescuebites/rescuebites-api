package com.rescuebites.api.users.facades.interfaces;

import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;

public interface IUserFacade {

    void ifEmailAlreadyExistsThrowException(String email);

    void verifyIfPasswordsMatch(String password, String confirmPassword);

    void validatePasswordOrThrowException(String rawPassword , User user);

    void ifUserIsNotEnabledThrowException(User user);

    void ifUserIsEnabledThrowException(User user);

    void validateTokenNotExpired(Token token);

    void validateResendLimit(User user);

    boolean validateAndCheckEmailChange(User user, String newEmail);

    void validatePasswordsIfProvided(String password, String confirmPassword);
}