package com.rescuebites.api.controllers.interfaces;

import com.rescuebites.api.controllers.requests.LoginRequest;
import com.rescuebites.api.controllers.requests.RegisterRequest;
import com.rescuebites.api.controllers.responses.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/auth")
public interface IAuthController {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    void registerUser(@RequestBody @Valid RegisterRequest registerRequest);

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    AuthResponse loginUser(@RequestBody @Valid LoginRequest loginRequest);
}
