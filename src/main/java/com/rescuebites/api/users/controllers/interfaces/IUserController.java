package com.rescuebites.api.users.controllers.interfaces;

import com.rescuebites.api.users.controllers.requests.ConfirmTokenRequest;
import com.rescuebites.api.users.controllers.requests.EmailRequest;
import com.rescuebites.api.users.controllers.requests.ResetPasswordRequest;
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

    @PostMapping("/reset-password/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendResetPasswordEmail(@RequestBody EmailRequest emailRequest);

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest);
}