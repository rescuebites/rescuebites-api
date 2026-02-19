package com.rescuebites.api.payment.mappers;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.data.enums.PaymentStatus;
import com.rescuebites.api.payment.data.models.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toPayment(Order order) {
        return Payment.builder()
                .order(order)
                .amount(order.getTotal())
                .status(PaymentStatus.PENDING)
                .build();
    }
}
