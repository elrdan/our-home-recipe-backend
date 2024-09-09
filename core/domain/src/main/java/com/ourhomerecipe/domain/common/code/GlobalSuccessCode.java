package com.ourhomerecipe.domain.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum GlobalSuccessCode {
	SUCCESS(200, "정상적으로 처리되었습니다.", HttpStatus.OK),
	CREATE(201, "정상적으로 생성되었습니다.", HttpStatus.CREATED);

	private final int code;
	private final String message;
	private final HttpStatus status;

	GlobalSuccessCode(int code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
