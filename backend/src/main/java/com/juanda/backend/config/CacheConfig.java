package com.juanda.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // ObjectMapper para fechas (LocalDate) y decimales
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());

        var serializer = new GenericJackson2JsonRedisSerializer(om);

        // TTL por defecto y formato de clave/valor
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(60))  // TTL global 60s
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues()
            .computePrefixWith(cacheName -> "hotel::" + cacheName + "::");

        // Puedes personalizar TTL por caché si quieres:
        // Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        // configs.put("search", defaultConfig.entryTtl(Duration.ofSeconds(60)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            // .withInitialCacheConfigurations(configs)
            .build();
    }

}
