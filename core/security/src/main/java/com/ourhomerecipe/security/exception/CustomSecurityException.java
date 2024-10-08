package com.ourhomerecipe.security.exception;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class CustomSecurityException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public CustomSecurityException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
