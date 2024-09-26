package com.ourhomerecipe.domain.common.exception;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class CustomRedisException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public CustomRedisException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}