package com.rescuebites.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        // Registrar caches conocidas para que existan en el arranque
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine);
        // Permitir null values para compatibilidad con Optional vacío etc.
        manager.setAllowNullValues(true);
        manager.setCacheNames(Arrays.asList(
                "localities",
                "commerceTypes",
                "activeProducts",
                "productsByCommerce",
                "activeProductsSortedByPrice",
                "activeProductsByCommerceTypeSortedByPrice",
                "productById"
        ));
        return manager;
    }
}
