package com.rescuebites.api.services.interfaces;

import com.rescuebites.api.controllers.requests.LoginRequest;
import com.rescuebites.api.controllers.requests.RegisterRequest;
import com.rescuebites.api.controllers.responses.AuthResponse;

public interface IAuthService{

    void register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
