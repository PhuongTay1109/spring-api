package com.tay.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private String id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private LocalDate dob; // date of birth
	
}
