package com.rescuebites.api.users.controllers.implementations;

import com.rescuebites.api.users.controllers.interfaces.IAuthController;
import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.RegisterRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;
import com.rescuebites.api.users.services.interfaces.IAuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController implements IAuthController {

    private final IAuthService authService;

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        this.authService.register(registerRequest);
    }

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        return this.authService.login(loginRequest);
    }
}
