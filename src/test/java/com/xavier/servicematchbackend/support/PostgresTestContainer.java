package com.xavier.servicematchbackend.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresTestContainer {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("servicematch_test")
            .withUsername("servicematch")
            .withPassword("servicematch");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (POSTGRES.isRunning()) {
                POSTGRES.stop();
            }
        }));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        ensureStarted();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.placeholders.timestamp_tz", () -> "timestamptz");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    private static synchronized void ensureStarted() {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
    }
}
