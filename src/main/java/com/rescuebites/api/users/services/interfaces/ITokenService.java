package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;

import java.util.UUID;

public interface ITokenService {
    Token saveUserToken(User newUser);

    Token findByTokenOrThrowException(UUID token);

    Token findLatestTokenByUser(User user);

    boolean canResendToken(User user);

    void deleteToken(Token token);

    void deleteTokensByUser(User user);
}
