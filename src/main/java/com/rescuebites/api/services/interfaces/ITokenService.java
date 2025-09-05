package com.rescuebites.api.services.interfaces;

import com.rescuebites.api.data.models.Token;
import com.rescuebites.api.data.models.User;

import java.util.UUID;

public interface ITokenService {
    Token saveUserToken(User newUser);

    Token findByTokenOrThrowException(UUID token);

    boolean canResendToken(User user);

    /*
    void deleteToken(Token token);

     */
}
