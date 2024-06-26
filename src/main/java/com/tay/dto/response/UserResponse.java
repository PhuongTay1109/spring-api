package com.tay.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class UserResponse {

	private String id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private LocalDate dob; // date of birth
	
}
