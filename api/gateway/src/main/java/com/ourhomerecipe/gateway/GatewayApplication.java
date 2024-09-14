package com.ourhomerecipe.gateway;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	// RouteLocator를 설정하는 Bean 생성
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
		@Value("${member-service-url}") String memberServiceUrl
	) {
		// 각 서비스에 맞는 라우트 설정 호출
		return builder.routes()
			.route("api-member", memberRoute(memberServiceUrl))  // Member 관련 API 경로 라우트
			.build();
	}

	// Member 서비스에 대한 라우트 설정
	private Function<PredicateSpec, Buildable<Route>> memberRoute(String memberServiceUrl) {
		return r -> r.path("/v1/member/**")
			.uri(memberServiceUrl);
	}
}
