package com.ourhomerecipe.infra.config;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers  // Testcontainers를 사용하여 독립된 MySQL 환경에서 테스트 실행
public abstract class BaseTest {
	// MySQL TestContainer 설정
	private static final String SQL_USER_NAME = "root";
	private static final String SQL_PASSWORD = "1234";
	private static final String SQL_DATABASE_NAME = "test";

	// Redis TestContainer 설정
	private static final int REDIS_PORT = 6379;

	@Container
	public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName(SQL_DATABASE_NAME)  // 데이터베이스 이름 설정
		.withUsername(SQL_USER_NAME)         // 사용자 이름 설정
		.withPassword(SQL_PASSWORD)         // 비밀번호 설정
		.withCopyFileToContainer(MountableFile.forClasspathResource("db/initdb.d/1-schema.sql"), "/docker-entrypoint-initdb.d/1-schema.sql")
		.withCopyFileToContainer(MountableFile.forClasspathResource("db/initdb.d/2-data.sql"), "/docker-entrypoint-initdb.d/2-data.sql");

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

	@LocalServerPort
	protected int port;

	protected RequestSpecification spec;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.spec = new RequestSpecBuilder()
			.setPort(port)
			.setBaseUri("http://localhost")
			.setBasePath("/v1")
			.addFilter(documentationConfiguration(restDocumentation))
			.build();

		RestAssured.port = port;
	}
}
