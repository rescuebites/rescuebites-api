package com.rescuebites.api.order.utils;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.data.enums.OrderStatus;

public class OrderStatusValidator {

    private OrderStatusValidator() {
    }

    public static void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new ValidationException(
                    String.format("Transición de estado inválida: %s -> %s",
                            currentStatus.getDisplayName(),
                            newStatus.getDisplayName())
            );
        }
    }

    public static boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY;
            case READY -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
