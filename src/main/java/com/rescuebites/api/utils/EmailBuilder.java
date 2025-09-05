package com.rescuebites.api.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmailBuilder {

    @Value("${frontend.url}")
    private String frontendUrl;

    public String buildConfirmAccount(String userEmail, UUID confirmationToken){
        String confirmationLink =  frontendUrl + "/confirm?token=" + confirmationToken;

        System.out.println("Generated token: " + confirmationToken);
        //helper.setSubject("Confirm your registration ✔");

        return """
                <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                    <h2 style="color: #2e6c80;">Welcome to Our Platform!</h2>
                    <p style="font-size: 16px;">Hello,</p>
                    <p style="font-size: 16px;">
                        Thank you for registering. Please confirm your email address by clicking the button below:
                    </p>
                    <a href="%s" style="display: inline-block; margin-top: 20px; 
                        padding: 12px 24px; background-color: #2e6c80; 
                        color: white; text-decoration: none; border-radius: 5px;">
                        Confirm Account
                    </a>
                    <p style="margin-top: 20px; font-size: 14px; color: #777;">
                        If you didn’t create this account, you can safely ignore this email.
                    </p>
                </div>
                """.formatted(confirmationLink);
    }

    public String buildResendConfirmAccount(String userEmail, UUID newToken) {
        String confirmationLink = frontendUrl + "/confirm?token=" + newToken;

        return """
            <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                <h2 style="color: #2e6c80;">Account Confirmation - New Link</h2>
                <p style="font-size: 16px;">Hello %s,</p>
                <p style="font-size: 16px;">
                    You (or someone else) requested a new confirmation link for your account.
                    Please confirm your email address by clicking the button below:
                </p>
                <a href="%s" style="display: inline-block; margin-top: 20px; 
                    padding: 12px 24px; background-color: #2e6c80; 
                    color: white; text-decoration: none; border-radius: 5px;">
                    Confirm Account
                </a>
                <p style="margin-top: 20px; font-size: 14px; color: #777;">
                    If you didn’t request this, you can safely ignore this email.
                </p>
            </div>
            """.formatted(userEmail, confirmationLink);
    }
}