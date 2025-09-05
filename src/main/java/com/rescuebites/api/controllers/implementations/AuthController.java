package com.rescuebites.api.controllers.implementations;

import com.rescuebites.api.controllers.interfaces.IAuthController;
import com.rescuebites.api.controllers.requests.LoginRequest;
import com.rescuebites.api.controllers.requests.RegisterRequest;
import com.rescuebites.api.controllers.responses.AuthResponse;
import com.rescuebites.api.services.interfaces.IAuthService;
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
