package com.rescuebites.api.order.data.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("Efectivo"),
    MERCADO_PAGO("Mercado Pago");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}