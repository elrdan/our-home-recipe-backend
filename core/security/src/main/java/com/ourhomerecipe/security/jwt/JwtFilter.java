package com.ourhomerecipe.security.jwt;

import static com.ourhomerecipe.domain.common.error.code.SecurityErrorCode.*;
import static org.springframework.util.StringUtils.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	private final String AUTHENTICATION_HEADER = "Authorization";		// HTTP 요청에서 사용될 인증 헤더의 이름
	private final String AUTHENTICATION_SCHEME = "Bearer ";				// Bearer 토큰

	private final JwtProvider jwtProvider;

	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try{
			String accessToken = extractToken(request);
			if(hasText(accessToken)) {
				jwtProvider.validate(accessToken);						// 토큰 유효성 검증

				// 토큰 권한과 DB에 존재하는 권한 비교하는 로직 추가 예정

				SecurityContextHolder.getContext()
					.setAuthentication(jwtProvider.toAuthentication(accessToken));	// 인증 객체 설정
			}
			filterChain.doFilter(request, response);
		}catch (Exception e) {
			handleException(e, response);
		}
	}

	/**
	 * 요청 헤더에서 JWT를 추출하고, Bearer 스키마를 기준으로 파싱
	 */
	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHENTICATION_HEADER);

		if(hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_SCHEME)){
			return bearerToken.substring(AUTHENTICATION_SCHEME.length());
		}

		throw new AuthenticationCredentialsNotFoundException("토큰이 존재하지 않습니다.");
	}

	/**
	 * Exception Handler
	 */
	private void handleException(Exception e, HttpServletResponse response) throws IOException {
		BaseErrorCode errorCode;

		if(e instanceof ExpiredJwtException) {
			// 토큰 만료
			errorCode = VALIDATION_TOKEN_EXPIRED;
			log.warn(">>>>> ExpiredJwtException : ", e);
		}else if(e instanceof AuthenticationCredentialsNotFoundException) {
			// 토큰이 없음 -> 토큰 추출 실패
			errorCode = VALIDATION_NOT_EXISTS_TOKEN_FAILED;
			log.warn(">>>>> AuthenticationCredentialsNotFoundException : ", e);
		}else if(e instanceof AccessDeniedException) {
			// 접근권한이 없음
			errorCode = VALIDATION_TOKEN_NOT_AUTHORIZATION;
			log.warn(">>>>> AccessDeniedException : ", e);
		}else {
			errorCode = VALIDATION_TOKEN_FAILED;
			log.warn(">>>>> TokenException : ", e);
		}

		response.setStatus(errorCode.getStatus().value()); // HTTP 상태 코드 설정
		response.setContentType("application/json"); // 응답의 Content-Type 설정
		response.setCharacterEncoding("UTF-8"); // 응답 문자 인코딩 설정

		// 에러 메시지 JSON 직렬화 및 응답 작성
		ObjectMapper objectMapper = new ObjectMapper();
		String responseMessage = objectMapper.writeValueAsString(errorCode.getErrorResponse());
		response.getWriter().write(responseMessage);
	}
}
