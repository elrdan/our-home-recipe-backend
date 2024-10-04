package com.ourhomerecipe.domain.common.error.code;

import org.springframework.http.HttpStatus;

import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum RecipeErrorCode implements BaseErrorCode {
	NOT_FOUND_RECIPE_DETAIL(400, "레시피 상세 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
	NOT_REGISTER_INGREDIENT(400, "등록되어 있지 않는 재료입니다.", HttpStatus.BAD_REQUEST),
	NOT_REGISTER_TAG(400, "등록되어 있지 않는 태그입니다.", HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	RecipeErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}

}
