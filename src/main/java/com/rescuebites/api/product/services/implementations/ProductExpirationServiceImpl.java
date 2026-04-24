package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.events.ProductsExpiredEvent;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IProductExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductExpirationServiceImpl implements IProductExpirationService {

    private final IProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(
            value = {"activeProducts", "productsByCommerce", "productById",
                     "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice"},
            allEntries = true
    )
    public void deactivateExpiredProducts() {
        LocalDate today = LocalDate.now();

        List<Product> expired = productRepository.findAllExpiredActiveProducts(today);

        if (expired.isEmpty()) {
            log.debug("[ExpirationScheduler] No se encontraron productos vencidos al día {}.", today);
            return;
        }

        log.info("[ExpirationScheduler] {} producto(s) vencido(s) detectado(s). Desactivando...", expired.size());

        LocalDateTime now = LocalDateTime.now();
        expired.forEach(p -> {
            p.setActive(false);
            p.setDeactivatedAt(now);
        });
        productRepository.saveAll(expired);

        // Agrupar por commerceId y publicar un evento por comercio
        Map<UUID, List<Product>> byCommerce = expired.stream()
                .collect(Collectors.groupingBy(p -> p.getCommerce().getCommerceId()));

        byCommerce.forEach((commerceId, products) -> {
            List<ProductsExpiredEvent.ExpiredProductInfo> infos = products.stream()
                    .map(p -> new ProductsExpiredEvent.ExpiredProductInfo(
                            p.getProductId(),
                            p.getName()
                    ))
                    .toList();

            ProductsExpiredEvent event = new ProductsExpiredEvent(commerceId, infos);
            eventPublisher.publishEvent(event);

            log.info("[ExpirationScheduler] Evento publicado para comercio {} con {} producto(s) vencido(s).",
                    commerceId, infos.size());
        });
    }
}
