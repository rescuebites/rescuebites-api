package com.rescuebites.api.payment.services.payment;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.data.models.Payment;
import com.rescuebites.api.payment.mappers.PaymentMapper;
import com.rescuebites.api.payment.repositories.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentInitializationService {

    private final IPaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public Payment getOrCreatePayment(UUID orderId, Order order) {
        return paymentRepository.findByOrderId(orderId)
                .orElse(paymentMapper.toPayment(order));
    }

    public void updatePreferenceId(Payment payment, String preferenceId) {
        payment.setMercadoPagoPreferenceId(preferenceId);
        paymentRepository.save(payment);
    }
}
