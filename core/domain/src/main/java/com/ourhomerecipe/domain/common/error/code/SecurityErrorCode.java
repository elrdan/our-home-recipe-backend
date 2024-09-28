package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum SecurityErrorCode implements BaseErrorCode {
	VALIDATION_TOKEN_FAILED(401, "정상적인 토큰이 아닙니다. 확인 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED),
	VALIDATION_NOT_EXISTS_TOKEN_FAILED(401, "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
	VALIDATION_TOKEN_EXPIRED(401, "토큰의 유효기한이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	VALIDATION_TOKEN_NOT_AUTHORIZATION(403, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	LOGOUT_TOKEN(401, "로그아웃 처리된 토큰입니다.", HttpStatus.UNAUTHORIZED);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	SecurityErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
