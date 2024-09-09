package com.ourhomerecipe.member.exception;

import static com.ourhomerecipe.domain.common.error.code.GlobalErrorCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;
import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error(">>>>> Internal Server Error : {}", ex);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
			.body(INTERNAL_SERVER_ERROR.getErrorResponse());
	}

	@ExceptionHandler(MemberException.class)
	protected ResponseEntity<ErrorResponse> handleMemberException(MemberException ex) {
		log.error(">>>>> MemberException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus())
			.body(errorCode.getErrorResponse());
	}
}
