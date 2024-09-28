package com.ourhomerecipe.domain.common.repository;

import static com.ourhomerecipe.domain.common.error.code.RedisErrorCode.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.ourhomerecipe.domain.common.exception.CustomRedisException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeSeconds;

	@Qualifier("redisTemplate")
	private final RedisTemplate<String, Object> redisTemplate;

	@Qualifier("customStringRedisTemplate")
	private final RedisTemplate<String, String> stringRedisTemplate;

	/**
	 * SET Email AuthCode AND Confirm - 이메일 인증, 확인정보 저장
	 */
	public void setEmailCodeAndConfirm(String email, String authCode, String confirm) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(email,
			Map.of(
				"authCode", authCode,
				"confirm", confirm
			), createExpireDuration(authCodeSeconds));
	}

	/**
	 * GET Email AuthCode AND Confirm - 이메일 인증, 확인정보 가져오기
	 */
	public Map getEmailCodeAndConfirm(String email) {
		Optional<Object> optionalResult = getKeyIfPresent(email);
		if(optionalResult.isPresent()) {
			return (Map)optionalResult.get();
		}else {
			throw new CustomRedisException(NOT_EXIST_EMAIL_INFO);
		}
	}

	/**
	 * DELETE Redis Key
	 */
	public void deleteRedisInfo(String email) {
		redisTemplate.delete(email);
	}

	/**
	 * SET refresh token - 리프래쉬 토큰 정보 저장
	 */
	public void setRedisRefreshToken(String email, String refreshToken, int expirationSeconds) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(email, Map.of("refreshToken", refreshToken), createExpireDuration(expirationSeconds));
	}

	/**
	 * GET refresh token - 리프래쉬 토큰 정보 가져오기
	 */
	public Map getRefreshToken(Long memberId) {
		Optional<Object> optionalResult = getKeyIfPresent(String.valueOf(memberId));
		if(optionalResult.isPresent()) {
			return (Map)optionalResult.get();
		}else {
			throw new CustomRedisException(NOT_EXIST_REFRESH_TOKEN);
		}
	}

	/**
	 * 블랙 리스트 토큰 저장
	 */
	public void setBlackListToken(String accessToken, long expirationSeconds) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.set(accessToken, "logout", createExpireDuration(expirationSeconds));
	}

	/**
	 * 블랙 리스트 토큰 조회
	 */
	public String getBlackListToken(String token) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		return valueOperations.get(token);
	}

	/**
	 * 만료 시간 생성: 밀리초 -> 초
	 */
	private Duration createExpireDuration(long milliseconds) {
		return Duration.ofSeconds(milliseconds / 1000);
	}

	/**
	 * redis key null 체크
	 */
	private Optional getKeyIfPresent(String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}
}