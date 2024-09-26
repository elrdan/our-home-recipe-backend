package com.ourhomerecipe.domain.common.error.response;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
	private final int errorCode;
	private final String errorMessage;
	private final Map<String, String> validation = new HashMap<>();
	private final Map<String, String> missingParams = new HashMap<>();

	public ErrorResponse(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public void addValidation(String field, String message) {
		validation.put(field, message);
	}

	public void addMissingParams(String param) {
		missingParams.put(param, param + " 값이 확인되지 않습니다.");
	}
}