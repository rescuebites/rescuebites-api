package com.rescuebites.api.product.listeners;

import com.rescuebites.api.product.events.ProductsExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener que reacciona al evento {@link ProductsExpiredEvent}.
 *
 * <p><strong>TODO (branch notificaciones SSE):</strong> implementar el cuerpo de
 * {@link #handleProductsExpired(ProductsExpiredEvent)} para enviar la notificación
 * SSE al comercio afectado usando el {@code SseEmitter} correspondiente.</p>
 *
 * <p>El evento lleva:
 * <ul>
 *   <li>{@code commerceId} — UUID del comercio al que hay que notificar.</li>
 *   <li>{@code expiredProducts} — lista de {@code ExpiredProductInfo} con
 *       {@code productId} y {@code productName} de cada producto desactivado.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductExpirationNotificationListener {

    // TODO (branch SSE): inyectar aquí el SseEmitterRegistry o servicio equivalente
    //  private final SseEmitterRegistry sseEmitterRegistry;

    @Async("taskExecutor")
    @EventListener
    public void handleProductsExpired(ProductsExpiredEvent event) {
        // TODO (branch SSE): enviar notificación SSE al comercio event.commerceId()
        //
        // Ejemplo de uso esperado:
        //   sseEmitterRegistry.sendToCommerce(
        //       event.commerceId(),
        //       SseNotification.productsExpired(event.expiredProducts())
        //   );

        log.info("[ProductExpirationNotificationListener] Evento recibido para comercio {}. " +
                "Productos vencidos: {}. Pendiente de implementación SSE.",
                event.commerceId(),
                event.expiredProducts().stream()
                        .map(p -> p.productName() + " (" + p.productId() + ")")
                        .toList());
    }
}
