package com.ourhomerecipe.security.config;

import static com.ourhomerecipe.domain.member.enums.RoleType.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ourhomerecipe.domain.common.repository.RedisRepository;
import com.ourhomerecipe.security.exception.CustomAccessDeniedHandler;
import com.ourhomerecipe.security.jwt.JwtFilter;
import com.ourhomerecipe.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
	private final JwtProvider jwtProvider;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final RedisRepository redisRepository;

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
	 * 모든 요청에 대해서 접근을 허용하는 SecurityChain
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain permitAllFilterChain(HttpSecurity http) throws Exception {
		httpSecuritySetting(http);
		http
			.securityMatchers(matcher -> matcher
				.requestMatchers(permitAllRequestMatchers()))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(permitAllRequestMatchers()).permitAll()
				.anyRequest().authenticated()
			);
		return http.build();
	}

	/**
	 * 정상적인 토큰을 보유한 회원에 대한 접근 권한을 검증하는 SecurityFilterChain
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
		httpSecuritySetting(http);
		http
			.securityMatchers(matcher -> matcher
				.requestMatchers(userAuthRequestMatchers())
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(userAuthRequestMatchers()).hasAnyAuthority(ROLE_ADMIN.name(), ROLE_USER.name())
				.anyRequest().authenticated()
			)
			.exceptionHandling(exception -> exception
				.accessDeniedHandler(customAccessDeniedHandler)
			)
			.addFilterBefore(new JwtFilter(jwtProvider, redisRepository), ExceptionTranslationFilter.class);
		return http.build();
	}

	/**
	 * 모든 요청 endpoint
	 */
	private RequestMatcher[] permitAllRequestMatchers() {
		List<RequestMatcher> requestMatchers = List.of(
			antMatcher(POST, "/member/register"),						// 회원 등록
			antMatcher(POST, "/member/login"),						// 회원 로그인
			antMatcher(POST, "/member/logout"),						// 회원 로그아웃
			antMatcher(GET, "/member/token/refresh"),					// 토큰 재발급


			antMatcher(POST, "/member/email/auth/request"),			// 이메일 인증 코드 요청
			antMatcher(POST, "/member/email/auth/confirm"),			// 이메일 인증 코드 확인

			antMatcher(POST, "/email/verification/send"),				// 이메일 인증 코드 메일 보내기

			antMatcher(GET, "/recipe/metadata"),						// 레시피 메타데이터
			antMatcher(GET, "/recipe/member/search"),					// 레시피 사용자(nickname) 조회
			antMatcher(GET, "/recipe/search"),						// 레시피 이름 조회
			antMatcher(GET, "/recipe/detail/guest/*"),				// 레시피 상세 조회(guest)

			// 스웨거
			antMatcher(GET, "/swagger-ui.html"),
			antMatcher(GET, "/swagger-ui/**"),
			antMatcher(GET, "/urls.json"),
			antMatcher(GET, "/openapi3.yaml")
		);

		return requestMatchers.toArray(RequestMatcher[]::new);
	}

	/**
	 * 어드민, 일반 회원 endpoint
	 */
	private RequestMatcher[] userAuthRequestMatchers() {
		List<RequestMatcher> requestMatchers = List.of(
			antMatcher(GET, "/member/search/*"),						// 회원 검색

			antMatcher(POST, "/member/follow/*"),						// 팔로우
			antMatcher(POST, "/member/unfollow/*"),					// 언팔로우

			antMatcher(GET, "/member/me/profile"),                	// 내 프로필 조회
			antMatcher(POST, "/member/me/profile"),                	// 내 프로필 수정

			antMatcher(POST, "/recipe/register"),						// 레시피 등록
			antMatcher(GET, "/recipe/detail/member/*")				// 레시피 상세 조회(member)
		);

		return requestMatchers.toArray(RequestMatcher[]::new);
	}

	/**
	 * 설정하지 않은 경로에 대해서 모든 요청을 거부하는 SecurityChain
	 */
	@Bean
	@Order(3)
	public SecurityFilterChain otherFilterChain(HttpSecurity http) throws Exception {
		httpSecuritySetting(http);
		http
			.securityMatchers(matcher -> matcher
				.requestMatchers("/**")
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(permitAllRequestMatchers()).permitAll()  // 허용 경로 적용
				.anyRequest().denyAll()
			)
			.exceptionHandling(exception -> exception
				.accessDeniedHandler(customAccessDeniedHandler));

		return http.build();
	}

	/**
	 * cors 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		// Todo 특정 origin을 제어 및 Detail한 설정 추가

		CorsConfiguration configuration = new CorsConfiguration();
		/**
		 * CorsConfiguration.setAllowCredentials(true)를 사용하면서
		 * addAllowedOriginPattern("*")을 설정한 경우,
		 * 크레덴셜(Credentials: 쿠키, 인증 헤더, TLS 인증서 등)이
		 * 포함된 요청은 출처가 와일드카드(*)일 때 CORS 규칙에 의해 거부됩니다.
		 */
		// 여러 출처 허용 (localhost와 127.0.0.1을 둘 다 허용)
		configuration.addAllowedOriginPattern("http://localhost:3000");
		configuration.addAllowedOriginPattern("http://127.0.0.1:3000");

		configuration.setAllowCredentials(true); // 크레덴셜 허용 설정
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
			// .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 정책
			.cors(AbstractHttpConfigurer::disable)
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
