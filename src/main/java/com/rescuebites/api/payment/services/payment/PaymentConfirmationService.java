package com.rescuebites.api.payment.services.payment;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.payment.data.enums.PaymentStatus;
import com.rescuebites.api.payment.data.models.Payment;
import com.rescuebites.api.payment.repositories.IPaymentRepository;
import com.rescuebites.api.shared.services.interfaces.IWhatsAppService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentConfirmationService {

    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IWhatsAppService whatsAppService;

    @Transactional
    public void confirmPayment(UUID orderId, String paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Pedido no encontrado"));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ValidationException("Registro de pago no encontrado"));

        // Evitar doble confirmación si el webhook ya lo procesó antes
        if (OrderStatus.CONFIRMED.equals(order.getStatus())) {
            return;
        }

        updatePaymentStatus(payment, paymentId);
        updateOrderStatus(order);
        notifyPaymentConfirmation(order);
    }

    /**
     * Confirma el pago usando el redirect-callback de Mercado Pago como fallback
     * cuando el webhook no llegó (sandbox, ngrok offline, etc).
     * No hace nada si el pedido ya fue confirmado (idempotente).
     */
    @Transactional
    public void confirmPaymentIfNotAlreadyConfirmed(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Pedido no encontrado"));

        if (OrderStatus.CONFIRMED.equals(order.getStatus())) {
            // Ya confirmado (probablemente por el webhook), no hacer nada
            return;
        }

        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);

        if (payment != null && PaymentStatus.APPROVED.equals(payment.getStatus())) {
            // Ya aprobado pero orden no confirmada: confirmar
            updateOrderStatus(order);
            notifyPaymentConfirmation(order);
        } else if (payment != null) {
            // Marcar el pago como aprobado y confirmar la orden
            updatePaymentStatus(payment, null);
            updateOrderStatus(order);
            notifyPaymentConfirmation(order);
        } else {
            // Sin registro de pago: solo actualizar estado de orden
            updateOrderStatus(order);
        }
    }

    private void updatePaymentStatus(Payment payment, String paymentId) {
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setMercadoPagoPaymentId(paymentId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void updateOrderStatus(Order order) {
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private void notifyPaymentConfirmation(Order order) {
        whatsAppService.notifyClientOrderConfirmation(order);
        whatsAppService.notifyCommerceNewOrder(order);
    }
}
