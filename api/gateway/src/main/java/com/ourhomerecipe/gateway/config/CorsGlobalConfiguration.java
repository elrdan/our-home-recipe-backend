package com.ourhomerecipe.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsGlobalConfiguration {

	@Bean
	public CorsWebFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 크레덴셜 허용 설정
		config.addAllowedOriginPattern("*"); // 모든 출처 허용
		config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
		config.addAllowedHeader("*"); // 모든 헤더 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}
}