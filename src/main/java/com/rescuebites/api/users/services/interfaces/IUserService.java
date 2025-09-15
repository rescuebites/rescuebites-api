package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.RegisterRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;
import com.rescuebites.api.users.data.models.User;

import java.util.UUID;

public interface IUserService {

    void saveUser(RegisterRequest registerRequest);

    User findByIdOrThrowException(UUID userId) ;

    User findUserByEmailOrThrowException(String email);

    void verifyNewUser(UUID token);

    void resendConfirmationEmail(String email);

    AuthResponse verifyUser(LoginRequest loginRequest);
}