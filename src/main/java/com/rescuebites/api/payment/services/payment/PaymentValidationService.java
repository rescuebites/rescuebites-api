package com.rescuebites.api.payment.services.payment;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.models.Order;
import org.springframework.stereotype.Service;

import static com.rescuebites.api.payment.utils.PaymentConstants.ACCEPTED_PAYMENT_METHODS;

@Service
public class PaymentValidationService {

    public void validateOrderForPayment(Order order) {
        validateOrderStatus(order);
        validatePaymentMethod(order);
    }

    private void validateOrderStatus(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ValidationException("El pedido no está en estado pendiente de pago");
        }
    }

    private void validatePaymentMethod(Order order) {
        if (!ACCEPTED_PAYMENT_METHODS.contains(order.getPaymentMethod())) {
            throw new ValidationException(
                    "Método de pago no soportado: " + order.getPaymentMethod() +
                    ". Métodos aceptados: " + ACCEPTED_PAYMENT_METHODS
            );
        }
    }
}
