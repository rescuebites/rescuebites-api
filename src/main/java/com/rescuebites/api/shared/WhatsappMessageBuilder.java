package com.rescuebites.api.shared;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.data.models.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsappMessageBuilder {

    public String buildCommerceNewOrderMessage(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("*NUEVO PEDIDO - RescueBites*\n\n");
        sb.append("Pedido: *").append(order.getOrderNumber()).append("*\n");
        sb.append("Cliente: ").append(order.getClient().getFirstName())
                .append(" ").append(order.getClient().getLastName()).append("\n");
        sb.append("Total: $").append(order.getTotal()).append("\n");
        sb.append("Pago: ").append(order.getPaymentMethod().getDisplayName()).append("\n\n");

        sb.append("*Productos:*\n");
        for (OrderItem item : order.getItems()) {
            sb.append("• ").append(item.getQuantity()).append("x ")
                    .append(item.getProductName())
                    .append(" - $").append(item.getSubtotal()).append("\n");
        }

        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            sb.append("\n📝 Notas: ").append(order.getNotes());
        }

        sb.append("\n\n✅ Por favor, confirma el pedido en la app.");

        return sb.toString();
    }

    public String buildCommerceCancelledOrderMessage(Order order) {
        return String.format(
                "*PEDIDO CANCELADO*\n\n" +
                        "📋 Pedido: *%s*\n" +
                        "👤 Cliente: %s %s\n" +
                        "💰 Total: $%s\n\n" +
                        "Motivo: %s",
                order.getOrderNumber(),
                order.getClient().getFirstName(),
                order.getClient().getLastName(),
                order.getTotal(),
                order.getCancellationReason() != null ? order.getCancellationReason() : "No especificado"
        );
    }

    public String buildClientStatusChangeMessage(Order order) {
        String statusEmoji = switch (order.getStatus()) {
            case CONFIRMED -> "✅";
            case PREPARING -> "👨‍🍳";
            case READY -> "🎉";
            case COMPLETED -> "✔️";
            case CANCELLED -> "❌";
            default -> "ℹ️";
        };

        String statusMessage = switch (order.getStatus()) {
            case CONFIRMED -> "Tu pedido ha sido confirmado y está siendo preparado";
            case PREPARING -> "Tu pedido está en preparación";
            case READY -> "¡Tu pedido está listo para retirar!";
            case COMPLETED -> "Pedido completado. ¡Gracias por tu compra!";
            case CANCELLED -> "Tu pedido ha sido cancelado";
            default -> "Estado actualizado";
        };

        return String.format(
                "%s *RescueBites - Actualización de Pedido*\n\n" +
                        "Pedido: *%s*\n" +
                        "Comercio: %s\n" +
                        "Estado: *%s*\n\n" +
                        "%s\n\n" +
                        "Para más detalles, ingresa a la app.",
                statusEmoji,
                order.getOrderNumber(),
                order.getCommerce().getName(),
                order.getStatus().getDisplayName(),
                statusMessage
        );
    }

    public String buildClientOrderConfirmationMessage(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("*PEDIDO CONFIRMADO - RescueBites*\n\n");
        sb.append("Pedido: *").append(order.getOrderNumber()).append("*\n");
        sb.append("Comercio: ").append(order.getCommerce().getName()).append("\n");
        sb.append("Total: $").append(order.getTotal()).append("\n\n");

        sb.append("📦 *Productos:*\n");
        for (OrderItem item : order.getItems()) {
            sb.append("• ").append(item.getQuantity()).append("x ")
                    .append(item.getProductName()).append("\n");
        }

        sb.append("\n📱 Puedes hacer seguimiento de tu pedido en la app.");
        sb.append("\n\n¡Gracias por usar RescueBites! 🌱");

        return sb.toString();
    }
}
