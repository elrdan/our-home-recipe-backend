package com.ourhomerecipe.infra.config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration
@Testcontainers
public class TestContainerConfig {
    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db/initdb.d/1-schema.sql");

    // MySQL Testcontainer에서 제공한 설정 정보를 동적으로 Spring에 반영
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);  // 데이터베이스 URL 등록
        registry.add("spring.datasource.username", mysql::getUsername);  // 사용자 이름 등록
        registry.add("spring.datasource.password", mysql::getPassword);  // 비밀번호 등록
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }
}