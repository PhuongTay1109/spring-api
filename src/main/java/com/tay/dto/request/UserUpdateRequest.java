package com.tay.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

	private String password;
	private String firstName;
	private String lastName;
	private LocalDate dob; // date of birth

}
