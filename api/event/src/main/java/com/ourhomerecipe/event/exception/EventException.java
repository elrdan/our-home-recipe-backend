package com.ourhomerecipe.event.exception;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class EventException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public EventException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
