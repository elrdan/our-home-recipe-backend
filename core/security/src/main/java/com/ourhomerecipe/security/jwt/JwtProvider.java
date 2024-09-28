package com.ourhomerecipe.security.jwt;

import static org.springframework.util.StringUtils.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ourhomerecipe.security.service.MemberDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
	private final String AUTHENTICATION_HEADER = "Authorization";        // HTTP 요청에서 사용될 인증 헤더의 이름
	private final String AUTHENTICATION_SCHEME = "Bearer ";                // Bearer 토큰
	private final String AUTHENTICATION_CLAIM_NAME = "roles";

	@Value("${jwt.access-expiration-milliseconds}")
	private int accessExpirationMilliseconds;        // 엑세스 토큰 만료 시간

	@Getter
	@Value("${jwt.refresh-expiration-milliseconds}")
	private int refreshExpirationMilliseconds;        // 리프래쉬 토큰 만료 시간

	@Value("${jwt.access-key}")
	private String accessSecret;                // Jwt 서명 엑세스 키

	/**
	 * 엑세스 토큰 생성
	 */
	public String createAccessToken(Authentication authentication) {
		Instant now = Instant.now();
		Date expiration = Date.from(now.plusSeconds(accessExpirationMilliseconds / 1000));
		SecretKey key = extractSecretKey();
		MemberDetailsImpl memberDetails = (MemberDetailsImpl)authentication.getPrincipal();

		String roles = "";
		// 사용자 권한을 roles에 추가
		if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
			roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(", "));
		}

		return Jwts.builder()
			.claim("id", memberDetails.getId())                            // 식별자 클레임 추가
			.setSubject(memberDetails.getUsername())                        // 이메일 subject로 설정
			.setIssuedAt(Date.from(now))                                    // 토큰 발급 시간 설정
			.setExpiration(expiration)                                        // 토큰 만료 시간 설정
			.claim(AUTHENTICATION_CLAIM_NAME, roles)                        // 권한 정보 클레임 추가
			.signWith(key, SignatureAlgorithm.HS512)                        // 알고리즘과 키값으로 서명
			.compact();
	}

	/**
	 * 리프래쉬 토큰 생성
	 */
	public String createRefreshToken(Authentication authentication) {
		Instant now = Instant.now();
		Date expiration = Date.from(now.plusSeconds(refreshExpirationMilliseconds / 1000));
		SecretKey key = extractSecretKey();
		MemberDetailsImpl memberDetails = (MemberDetailsImpl)authentication.getPrincipal();

		return Jwts.builder()
			.claim("id", memberDetails.getId())                            // 식별자 클레임 추가
			.setSubject(memberDetails.getUsername())                        // 이메일 subject로 설정
			.setIssuedAt(Date.from(now))                                    // 토큰 발급 시간 설정
			.setExpiration(expiration)                                        // 토큰 만료 시간 설정
			.signWith(key, SignatureAlgorithm.HS512)                        // 알고리즘과 키값으로 서명
			.compact();
	}

	/**
	 * 권한 체크
	 */
	public Authentication toAuthentication(String token) {
		JwtParser jwtParser = Jwts.parserBuilder()
			.setSigningKey(extractSecretKey())
			.build();
		Claims claims = jwtParser.parseClaimsJws(token).getBody();

		Object roles = claims.get(AUTHENTICATION_CLAIM_NAME);
		List<GrantedAuthority> authorities = null;
		if (roles != null && !roles.toString().trim().isEmpty()) {
			authorities = List.of(new SimpleGrantedAuthority(roles.toString()));
		}

		UserDetails user = MemberDetailsImpl.builder()
			.id(claims.get("id", Long.class))
			.email(claims.getSubject())
			.password(null)
			.nickname(claims.get("nickname", String.class))
			.authorities(authorities)
			.build();

		return new UsernamePasswordAuthenticationToken(user, token, authorities);
	}

	/**
	 * 요청 헤더에서 JWT를 추출하고, Bearer 스키마를 기준으로 파싱
	 */
	public String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHENTICATION_HEADER);

		if (hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_SCHEME)) {
			return bearerToken.substring(AUTHENTICATION_SCHEME.length());
		}

		return null;
	}

	/**
	 * 토큰 만료 시간 반환
	 */
	public long getExpiration(String token) {
		JwtParser jwtParser = Jwts.parserBuilder()
			.setSigningKey(extractSecretKey())
			.build();

		Date expiration = jwtParser.parseClaimsJws(token)
			.getBody()
			.getExpiration();

		return expiration.getTime() - new Date().getTime();
	}

	/**
	 * 토큰 검증
	 */
	public boolean validate(String token) {
		try {
			JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(extractSecretKey())
				.build();

			jwtParser.parseClaimsJws(token);
			return true;
		}catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * SecretKey 추출
	 */
	private SecretKey extractSecretKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
	}
}
