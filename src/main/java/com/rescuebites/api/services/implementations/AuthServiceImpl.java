package com.rescuebites.api.services.implementations;

import com.rescuebites.api.controllers.requests.LoginRequest;
import com.rescuebites.api.controllers.requests.RegisterRequest;
import com.rescuebites.api.controllers.responses.AuthResponse;
import com.rescuebites.api.services.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserServiceImpl userService;

    @Override
    public void register(RegisterRequest registerRequest) {
        userService.saveUser(registerRequest);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return this.userService.verifyUser(loginRequest);
    }
}
