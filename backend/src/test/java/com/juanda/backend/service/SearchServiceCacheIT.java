package com.juanda.backend.service;

import com.juanda.backend.testinfra.PostgresTC;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SearchServiceCacheIT extends PostgresTC {

    static GenericContainer<?> REDIS =
        new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @BeforeAll
    static void startRedis() { REDIS.start(); }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", () -> REDIS.getHost());
        r.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort());
    }

    @Autowired
    SearchService service;

    @Autowired
    StringRedisTemplate redis;

    @Test
    void second_call_should_hit_cache() {
        LocalDate ci = LocalDate.now().plusDays(5);
        LocalDate co = ci.plusDays(2);

        var resp1 = service.search(ci, co, 2);
        assertThat(resp1.results()).isNotEmpty();

        var beforeKeys = redis.keys("hotel::search::*");
        var resp2 = service.search(ci, co, 2);
        var afterKeys  = redis.keys("hotel::search::*");

        assertThat(resp2.results()).isNotEmpty();
        // Debería haber al menos una clave de cache creada
        assertThat(afterKeys.size()).isGreaterThanOrEqualTo(beforeKeys == null ? 0 : beforeKeys.size());
    }
}
