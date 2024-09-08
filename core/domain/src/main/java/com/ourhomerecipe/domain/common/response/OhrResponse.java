package com.ourhomerecipe.domain.common.response;

import static com.ourhomerecipe.domain.common.code.GlobalSuccessCode.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ourhomerecipe.domain.common.code.GlobalSuccessCode;

import lombok.Getter;

@Getter
public class OhrResponse<T> {
	private int code;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	public OhrResponse(T data) {
		this.code = SUCCESS.getCode();
		this.message = SUCCESS.getMessage();
		this.data = data;
	}

	public OhrResponse(GlobalSuccessCode statusCode, T data) {
		this.code = statusCode.getCode();
		this.message = statusCode.getMessage();
		this.data = data;
	}
}
