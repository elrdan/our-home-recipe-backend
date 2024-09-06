package com.ourhomerecipe.security.config;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

	/**
	 * Password Encoder
	 * DelegatingPasswordEncoder를 사용하여 다양한 인코딩 방식을 지원하며, 자동으로 가장 적합한 인코더를 사용
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * AuthenticationManager
	 * 로그인 인증 할때 사용
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * 사전 요청(Preflight Request) 확인 허용
	 * 서버의 허용 메서드를 검사하는 기능 및 CORS 정책을 확인하는 용도
	 */
	@Bean
	public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
		httpSecuritySetting(http);
		http
			.securityMatchers(matcher -> matcher
				.requestMatchers(OPTIONS, "/**")
			);

		return http.build();
	}

	/**
	 * cors 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		// Todo 특정 origin을 제어 및 Detail한 설정 추가

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true); // 크레덴셜 허용 설정
		configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
		configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
		configuration.addAllowedHeader("*"); // 모든 헤더 허용
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용

		return source;
	}

	/**
	 * Http 보안 구성 설정
	 */
	private void httpSecuritySetting(HttpSecurity http) throws Exception {
		http
			// jwt, OAuth 토큰을 사용 -> OAuth의 경우는 이슈가 발생할 수 있음 OAuth 구성할때 체크
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 정책
			.formLogin(AbstractHttpConfigurer::disable) // form 기반 로그인을 사용하지 않음.
			.httpBasic(AbstractHttpConfigurer::disable) // 기본으로 제공하는 http 사용하지 않음
			.rememberMe(AbstractHttpConfigurer::disable) // 토큰 기반이므로 세션 기반의 인증 사용하지 않음
			.headers(headers -> headers.frameOptions(
				HeadersConfigurer.FrameOptionsConfig::disable)) // x-Frame-Options 헤더 비활성화, 클릭재킹 공격 관련
			.logout(AbstractHttpConfigurer::disable) // stateful 하지 않기때문에 필요하지 않음
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 생성을 하지 않음
			)
			.anonymous(AbstractHttpConfigurer::disable); // 익명 사용자 접근 제한, 모든 요청이 인증 필요
	}
}
