package com.ourhomerecipe.infra.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers  // Testcontainers를 사용하여 독립된 MySQL 환경에서 테스트 실행
public class TestContainerConfig {

        // MySQL Testcontainer 설정: 테스트마다 새로운 MySQL 환경을 제공
        @Container
        public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("testdb")  // 데이터베이스 이름 설정
                .withUsername("test")         // 사용자 이름 설정
                .withPassword("test")         // 비밀번호 설정
                .withInitScript("db/initdb.d/1-schema.sql"); // 초기 스키마 설정

        // MySQL Testcontainer에서 제공한 설정 정보를 동적으로 Spring에 반영
        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.datasource.url", mysql::getJdbcUrl);  // 데이터베이스 URL 등록
            registry.add("spring.datasource.username", mysql::getUsername);  // 사용자 이름 등록
            registry.add("spring.datasource.password", mysql::getPassword);  // 비밀번호 등록
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        }
    }
