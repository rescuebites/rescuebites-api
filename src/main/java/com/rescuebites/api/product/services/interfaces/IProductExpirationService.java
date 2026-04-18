package com.rescuebites.api.product.services.interfaces;

/**
 * Servicio responsable de detectar productos vencidos,
 * desactivarlos del catálogo y publicar el evento correspondiente
 * para que el módulo de notificaciones pueda alertar al comercio.
 */
public interface IProductExpirationService {

    /**
     * Busca todos los productos activos cuya fecha de vencimiento sea anterior a hoy,
     * los desactiva y publica un {@code ProductsExpiredEvent} por cada comercio afectado.
     *
     * <p>Este método es invocado desde el scheduler diario; también puede llamarse
     * manualmente desde tests o desde un endpoint de administración.</p>
     */
    void deactivateExpiredProducts();
}
