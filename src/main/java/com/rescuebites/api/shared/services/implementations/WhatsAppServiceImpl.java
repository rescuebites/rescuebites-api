package com.rescuebites.api.shared.services.implementations;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.shared.WhatsappMessageBuilder;
import com.rescuebites.api.shared.services.interfaces.IWhatsAppService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppServiceImpl implements IWhatsAppService {

    @Value("${twilio.whatsapp-from}")
    private String whatsappFrom;

    private final WhatsappMessageBuilder whatsappMessageBuilder;

    @Override
    public void notifyCommerceNewOrder(Order order) {
        String message = whatsappMessageBuilder.buildCommerceNewOrderMessage(order);
        String commercePhone = order.getCommerce().getPhone();

        sendMessage(commercePhone, message);

        log.info("Notificación enviada al comercio {} para pedido {}",
                order.getCommerce().getName(), order.getOrderNumber());
    }

    @Override
    public void notifyCommerceCancelledOrder(Order order) {
        String message = whatsappMessageBuilder.buildCommerceCancelledOrderMessage(order);
        String commercePhone = order.getCommerce().getPhone();

        sendMessage(commercePhone, message);

        log.info("Notificación de cancelación enviada al comercio {} para pedido {}",
                order.getCommerce().getName(), order.getOrderNumber());
    }

    @Override
    public void notifyClientOrderStatusChange(Order order) {
        String message = whatsappMessageBuilder.buildClientStatusChangeMessage(order);
        String clientPhone = order.getClient().getPhone();

        sendMessage(clientPhone, message);

        log.info("Notificación de cambio de estado enviada al cliente para pedido {}",
                order.getOrderNumber());
    }

    @Override
    public void notifyClientOrderConfirmation(Order order) {
        String message = whatsappMessageBuilder.buildClientOrderConfirmationMessage(order);
        String clientPhone = order.getClient().getPhone();

        sendMessage(clientPhone, message);

        log.info("Confirmación de pedido enviada al cliente para pedido {}",
                order.getOrderNumber());
    }

    private void sendMessage(String toPhone, String messageBody) {
        try {
            // Formato de teléfono argentino: +54911XXXXXXXX
            String formattedPhone = formatArgentinePhone(toPhone);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhone),
                    new PhoneNumber(whatsappFrom),
                    messageBody
            ).create();

            log.info("Mensaje de WhatsApp enviado exitosamente a {}: SID {}",
                    formattedPhone, message.getSid());

        } catch (Exception e) {
            log.error("Error enviando mensaje de WhatsApp a {}: {}", toPhone, e.getMessage());
            // No lanzar excepción para no bloquear el flujo principal
        }
    }

    private String formatArgentinePhone(String phone) {
        // Remover espacios y caracteres especiales
        String cleaned = phone.replaceAll("[^0-9]", "");

        // Si empieza con 54, está bien formateado
        if (cleaned.startsWith("54")) {
            return "+" + cleaned;
        }

        // Si empieza con 9 (celular), agregar +54
        if (cleaned.startsWith("9")) {
            return "+54" + cleaned;
        }

        // Si no tiene prefijo, asumir que es un celular argentino
        return "+549" + cleaned;
    }
}