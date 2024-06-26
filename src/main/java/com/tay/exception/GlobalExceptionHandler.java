package com.tay.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tay.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// để bắt các exception ngoài các exception đã bắt dưới kia
	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
		apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}

	@ExceptionHandler(value = RuntimeException.class)
	ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException exception) {
		String enumKey = exception.getFieldError().getDefaultMessage();
		ErrorCode errorCode = ErrorCode.valueOf(enumKey);
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}

	@ExceptionHandler(value = AppException.class)
	ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}

}
