package com.rescuebites.api.controllers.implementations;

import com.rescuebites.api.controllers.interfaces.IUserController;
import com.rescuebites.api.controllers.requests.ConfirmTokenRequest;
import com.rescuebites.api.controllers.requests.EmailRequest;
import com.rescuebites.api.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements IUserController {

    private final IUserService userService;

    @Override
    public void verifyAccount(ConfirmTokenRequest confirmTokenRequest) {
        userService.verifyNewUser(confirmTokenRequest.token());
    }

    @Override
    public void resendConfirmationEmail(EmailRequest emailRequest) {
        userService.resendConfirmationEmail(emailRequest.email());
    }
}