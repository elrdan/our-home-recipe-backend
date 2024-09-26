package com.ourhomerecipe.event.exception;

import static com.ourhomerecipe.domain.common.error.code.GlobalErrorCode.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ourhomerecipe.domain.common.error.code.BaseErrorCode;
import com.ourhomerecipe.domain.common.error.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error(">>>>> Internal Server Error : {}", ex);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
			.body(INTERNAL_SERVER_ERROR.getErrorResponse());
	}

	@ExceptionHandler(EventException.class)
	protected ResponseEntity<ErrorResponse> handleMemberException(EventException ex) {
		log.error(">>>>> MailException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus())
			.body(errorCode.getErrorResponse());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error(">>>>> validation Failed : {}", ex);
		BindingResult bindingResult = ex.getBindingResult();

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		ErrorResponse errorResponse = VALIDATION_FAILED.getErrorResponse();
		fieldErrors.forEach(error -> errorResponse.addValidation(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(VALIDATION_FAILED.getStatus()).body(errorResponse);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParamsException(MissingServletRequestParameterException ex) {
		log.error(">>>>> MissingServletRequestParameterException : {}", ex);
		ErrorResponse errorResponse = MISSING_REQUEST_PARAM.getErrorResponse();

		errorResponse.addMissingParams(ex.getParameterName());
		return ResponseEntity.status(MISSING_REQUEST_PARAM.getStatus()).body(errorResponse);
	}
}
