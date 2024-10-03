package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum S3ErrorCode implements BaseErrorCode {
	S3_UPLOAD_FAILED(500, "S3에 파일을 업로드하는 과정에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	S3ErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
