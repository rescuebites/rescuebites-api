package com.rescuebites.api.product.schedulers;

import com.rescuebites.api.product.services.interfaces.IProductExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler que dispara diariamente la detección de productos vencidos.
 *
 * <p>Se ejecuta todos los días a las 00:05 (5 minutos después de medianoche)
 * para asegurarse de que el día ya haya cambiado antes de comparar fechas.</p>
 *
 * <p>Al finalizar, el servicio publica un {@code ProductsExpiredEvent} por cada
 * comercio afectado. El módulo de notificaciones SSE (otra rama) es el responsable
 * de escuchar ese evento y enviar la alerta en tiempo real al comercio.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductExpirationScheduler {

    private final IProductExpirationService productExpirationService;

    @Scheduled(cron = "0 5 0 * * *")
    public void checkExpiredProducts() {
        log.info("[ExpirationScheduler] Iniciando verificación de productos vencidos...");
        productExpirationService.deactivateExpiredProducts();
        log.info("[ExpirationScheduler] Verificación de productos vencidos completada.");
    }
}
