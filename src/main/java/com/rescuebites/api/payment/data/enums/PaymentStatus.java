package com.rescuebites.api.payment.data.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado"),
    CANCELLED("Cancelado"),
    IN_PROCESS("En proceso");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

}