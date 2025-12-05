package com.rescuebites.api.users.controllers.interfaces;

import com.rescuebites.api.users.controllers.requests.ConfirmTokenRequest;
import com.rescuebites.api.users.controllers.requests.EmailRequest;
import com.rescuebites.api.users.controllers.requests.ResetPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/users")
public interface IUserController {

    //Después del registro, el usuario debe verificar su cuenta
    @PostMapping("/{userId}/verify-account")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void verifyAccount(@PathVariable UUID userId,
                       @RequestBody @Valid ConfirmTokenRequest confirmTokenRequest);

    @PostMapping("/resend-verification-account")
    @ResponseStatus(HttpStatus.OK)
    void resendConfirmationEmail(@RequestBody @Valid EmailRequest emailRequest);

    @PostMapping("/reset-password/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sendResetPasswordEmail(@RequestBody @Valid EmailRequest emailRequest);

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest);
}