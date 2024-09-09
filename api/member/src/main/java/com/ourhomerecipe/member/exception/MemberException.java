package com.ourhomerecipe.member.exception;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public MemberException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
