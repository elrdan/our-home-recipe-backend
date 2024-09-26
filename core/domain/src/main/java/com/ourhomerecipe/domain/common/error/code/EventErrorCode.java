package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum EventErrorCode implements BaseErrorCode {
	UNABLE_TO_SEND_EMAIL(504, "이메일 전송에 실패했습니다.", HttpStatus.GATEWAY_TIMEOUT);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	EventErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
