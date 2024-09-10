package com.ourhomerecipe.member.exception;

import static com.ourhomerecipe.domain.common.error.code.GlobalErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

	// 유효성 검사 실패 처리 (MethodArgumentNotValidException 처리 추가)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		// 기본적인 에러 응답 생성
		ErrorResponse errorResponse = new ErrorResponse(400, "유효성 검사 오류");

		// 유효성 검사에서 실패한 필드들에 대해 메시지 추가
		ex.getBindingResult().getFieldErrors().forEach(error ->
				errorResponse.addValidation(error.getField(), error.getDefaultMessage())
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
