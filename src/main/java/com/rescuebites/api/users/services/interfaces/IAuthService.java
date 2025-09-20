package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.RegisterRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;

public interface IAuthService{

    void register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
