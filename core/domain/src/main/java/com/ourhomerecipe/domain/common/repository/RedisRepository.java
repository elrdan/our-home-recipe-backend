package com.ourhomerecipe.domain.common.repository;

import static com.ourhomerecipe.domain.common.error.code.RedisErrorCode.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

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
	private int authCodeSeconds;

	private final RedisTemplate<String, Object> redisTemplate;

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
	 * 만료 시간 생성
	 */
	private Duration createExpireDuration(int seconds) {
		return Duration.ofSeconds(seconds);
	}

	/**
	 * redis key null 체크
	 */
	private Optional getKeyIfPresent(String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}
}