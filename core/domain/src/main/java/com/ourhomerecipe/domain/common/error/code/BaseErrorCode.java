package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

public interface BaseErrorCode {
	int getErrorCode();

	String getErrorMessage();

	HttpStatus getStatus();

	ErrorResponse getErrorResponse();
}