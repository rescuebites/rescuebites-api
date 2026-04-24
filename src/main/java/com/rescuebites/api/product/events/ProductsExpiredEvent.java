package com.rescuebites.api.product.events;

import java.util.List;
import java.util.UUID;

/**
 * Evento publicado cuando uno o más productos de un comercio son desactivados
 * automáticamente por haber superado su fecha de vencimiento.
 *
 * El branch de notificaciones SSE debe escuchar este evento para enviar
 * la alerta en tiempo real al comercio correspondiente.
 *
 * @param commerceId       ID del comercio al que pertenecen los productos vencidos.
 * @param expiredProducts  Lista reducida con el ID y nombre de cada producto vencido.
 */
public record ProductsExpiredEvent(
        UUID commerceId,
        List<ExpiredProductInfo> expiredProducts
) {

    /**
     * Información mínima de un producto vencido, pensada para ser transportada
     * de forma segura en un evento (sin entidades JPA / proxies Hibernate).
     */
    public record ExpiredProductInfo(
            UUID productId,
            String productName
    ) {}
}
