package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum MemberErrorCode implements BaseErrorCode {
	EXISTS_MEMBER_EMAIL(409, "이미 등록된 회원 이메일입니다.", HttpStatus.CONFLICT),
	EXISTS_MEMBER_PHONE_NUMBER(409, "이미 등록된 휴대폰 번호입니다.", HttpStatus.CONFLICT),
	EXISTS_MEMBER_NICKNAME(409, "이미 등록된 닉네임입니다.", HttpStatus.CONFLICT),
	NOT_MATCHED_PASSWORD(400, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
	NOT_MATCHED_AUTH_CODE(401, "인증 코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
	NOT_AUTHENTICATED_EMAIL(400, "이메일 인증이 되지 않았습니다. 이메일 인증 후 다시 시도해 주세요.", HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	MemberErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
