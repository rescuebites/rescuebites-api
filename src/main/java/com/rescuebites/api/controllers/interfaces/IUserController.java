package com.rescuebites.api.controllers.interfaces;

import com.rescuebites.api.controllers.requests.ConfirmTokenRequest;
import com.rescuebites.api.controllers.requests.EmailRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/users")
public interface IUserController {

    //Después del registro, el usuario debe verificar su cuenta
    @PostMapping("/verify-account")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void verifyAccount(@RequestBody ConfirmTokenRequest confirmTokenRequest);

    @PostMapping("/resend-verification-account")
    @ResponseStatus(HttpStatus.OK)
    void resendConfirmationEmail(@RequestBody EmailRequest emailRequest);
}