package com.rescuebites.api.commerce.data.projections;

import java.util.UUID;

public interface CommerceWebhookSecretProjection {
    UUID getCommerceId();
    String getMercadoPagoWebhookSecret();
}
