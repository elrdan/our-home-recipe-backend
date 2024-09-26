package com.ourhomerecipe.infra.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers  // Testcontainers를 사용하여 독립된 MySQL 환경에서 테스트 실행
public class TestContainerConfig {
	private static final String SQL_USER_NAME = "root";
	private static final String SQL_PASSWORD = "1234";
	private static final String SQL_DATABASE_NAME = "test";

	private static final int REDIS_PORT = 6379;

	// MySQL Testcontainer 설정: 테스트마다 새로운 MySQL 환경을 제공
	@Container
	public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName(SQL_DATABASE_NAME)  // 데이터베이스 이름 설정
		.withUsername(SQL_USER_NAME)         // 사용자 이름 설정
		.withPassword(SQL_PASSWORD)         // 비밀번호 설정
		.withInitScript("db/initdb.d/1-schema.sql"); // 초기 스키마 설정

	// MySQL Testcontainer에서 제공한 설정 정보를 동적으로 Spring에 반영
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);  // 데이터베이스 URL 등록
		registry.add("spring.datasource.username", mysql::getUsername);  // 사용자 이름 등록
		registry.add("spring.datasource.password", mysql::getPassword);  // 비밀번호 등록
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}

	@Container
	static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2-alpine")
		.withExposedPorts(REDIS_PORT);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.host", redisContainer::getHost);
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT));
	}
}
