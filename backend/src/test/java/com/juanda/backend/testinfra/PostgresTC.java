package com.juanda.backend.testinfra;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgresTC {

    // Usa una imagen ligera de Postgres 16
    protected static final PostgreSQLContainer<?> PG =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("hotel")
            .withUsername("dev")
            .withPassword("dev");

    @BeforeAll
    static void start() {
        if (!PG.isRunning()) {
            PG.start();
        }
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", PG::getJdbcUrl);
        r.add("spring.datasource.username", PG::getUsername);
        r.add("spring.datasource.password", PG::getPassword);
        // Aseguramos que Flyway corra como en runtime
        r.add("spring.flyway.enabled", () -> "true");
        // Validar esquema vs. entidades
        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

}
