package com.rescuebites.api.payment.services.implementations;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentWebhookData;
import com.rescuebites.api.payment.data.models.Payment;
import com.rescuebites.api.exceptions.custom_exceptions.IgnorableWebhookException;
import com.rescuebites.api.payment.repositories.IPaymentRepository;
import com.rescuebites.api.payment.services.interfaces.IMercadoPagoService;
import com.rescuebites.api.payment.services.interfaces.IPaymentService;
import com.rescuebites.api.payment.services.payment.PaymentConfirmationService;
import com.rescuebites.api.payment.services.payment.PaymentInitializationService;
import com.rescuebites.api.payment.services.payment.PaymentValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;
    private final IMercadoPagoService mercadoPagoService;
    private final PaymentValidationService paymentValidationService;
    private final PaymentInitializationService paymentInitializationService;
    private final PaymentConfirmationService paymentConfirmationService;

    @Override
    @Transactional
    public PaymentLinkResponse createPaymentPreference(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        paymentValidationService.validateOrderForPayment(order);

        Payment payment = paymentInitializationService.getOrCreatePayment(orderId, order);
        paymentRepository.save(payment);

        PaymentLinkResponse response = mercadoPagoService.createPaymentPreference(order);

        paymentInitializationService.updatePreferenceId(payment, response.preferenceId());

        return response;
    }

    @Override
    @Transactional
    public void processWebhookNotification(String notification) {
        try {
            mercadoPagoService.processWebhookNotification(notification)
                    .ifPresent(this::handleWebhookData);
        } catch (IgnorableWebhookException ignored) {
            // Webhooks de verificación/ping sin datos válidos - ignorar silenciosamente
        } catch (ValidationException e) {
            log.error("Error de validación procesando webhook: {}", e.getMessage());
            throw e;
        }
    }

    private void handleWebhookData(PaymentWebhookData webhookData) {
        switch (webhookData.status()) {
            case "approved" -> paymentConfirmationService.confirmPayment(webhookData.orderId(), webhookData.paymentId());
            case "pending" -> {}
            case "rejected" -> log.warn("Pago rechazado para pedido: {}", webhookData.orderId());
            default -> log.warn("Estado de pago desconocido: {}", webhookData.status());
        }
    }

    @Override
    @Transactional
    public void confirmPayment(UUID orderId, String paymentId) {
        paymentConfirmationService.confirmPayment(orderId, paymentId);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Pedido no encontrado"));
    }
}