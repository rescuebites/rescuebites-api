package com.rescuebites.api.order.data.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pendiente"),           // Pedido creado pero no confirmado
    CONFIRMED("Confirmado"),        // Pedido confirmado y pagado
    PREPARING("En preparación"),    // Comercio preparando el pedido (después de aceptar el nuevo pedido)
    READY("Listo para retirar"),   // Pedido listo
    COMPLETED("Completado"),        // Pedido entregado
    CANCELLED("Cancelado");         // Pedido cancelado

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}