package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum RedisErrorCode implements BaseErrorCode {
	NOT_EXIST_EMAIL_INFO(400, "이메일 인증 정보가 확인되지 않습니다. 확인 후 다시 시도해주세요.", HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	RedisErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}