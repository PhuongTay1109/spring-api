package com.tay.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// define sẵn các lỗi
// sau này cần quăng ra custom exception thì quăng ra AppException(ErrorCode....)
// code này là các mã lỗi tự quy định định nghĩa với nhau trong dự án
// không phải là mã lỗi HTTP

@AllArgsConstructor
@Getter
public enum ErrorCode {

	UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
	USER_EXISTED(1001, "User existed"),
	USER_NOT_FOUND(1002, "User not found"),
	USERNAME_INVALID(1003, "Username must be at least 3 characters"),
	PASSWORD_INVALID(1004, "Password must be at least 8 characters"),
	USER_NOT_EXISTED(1005, "User not existed"),
	UNAUTHENTICATED(1006, "Unauthenticated");


	private int code;
	private String message;

}
