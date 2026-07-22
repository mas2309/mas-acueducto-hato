package com.mas.co.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de cache con Caffeine.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // Nombres de caches como constantes para evitar strings sueltos
    public static final String CACHE_VALORES = "valores";
    public static final String CACHE_USUARIOS_ACTIVOS = "usuariosActivos";

    @Bean
    public CacheManager cacheManager() {
        // valores: tarifas y cargo fijo — cambian muy raramente, TTL 24 horas
        CaffeineCache cacheValores = new CaffeineCache(CACHE_VALORES,
            Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(10)
                .recordStats()
                .build());

        // usuariosActivos: lista para formularios — TTL 10 minutos
        CaffeineCache cacheUsuarios = new CaffeineCache(CACHE_USUARIOS_ACTIVOS,
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1)
                .recordStats()
                .build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(cacheValores, cacheUsuarios));
        return manager;
    }
}
