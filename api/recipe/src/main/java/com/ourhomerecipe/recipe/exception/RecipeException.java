package com.ourhomerecipe.recipe.exception;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class RecipeException extends RuntimeException {
	private final BaseErrorCode errorCode;

	public RecipeException(BaseErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
