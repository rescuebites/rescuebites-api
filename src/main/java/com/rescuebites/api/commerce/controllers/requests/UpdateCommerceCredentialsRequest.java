package com.rescuebites.api.commerce.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommerceCredentialsRequest {

    @NotBlank(message = "El access token es obligatorio")
    private String mercadoPagoAccessToken;

    private String mercadoPagoWebhookSecret;
}
