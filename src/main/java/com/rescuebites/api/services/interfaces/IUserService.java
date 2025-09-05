package com.rescuebites.api.services.interfaces;

import com.rescuebites.api.controllers.requests.LoginRequest;
import com.rescuebites.api.controllers.requests.RegisterRequest;
import com.rescuebites.api.controllers.responses.AuthResponse;
import com.rescuebites.api.data.models.User;

import java.util.UUID;

public interface IUserService {

    void saveUser(RegisterRequest registerRequest);

    User findByIdOrThrowException(UUID userId) ;

    User findUserByEmailOrThrowException(String email);

    void verifyNewUser(UUID token);

    void resendConfirmationEmail(String email);

    AuthResponse verifyUser(LoginRequest loginRequest);
}