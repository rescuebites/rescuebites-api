package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.UserRegistrationRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;

public interface IAuthService{

    void register(UserRegistrationRequest userRegistrationRequest);

    AuthResponse login(LoginRequest loginRequest);
}
