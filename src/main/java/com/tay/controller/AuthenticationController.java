package com.tay.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.tay.dto.request.AuthenticationRequest;
import com.tay.dto.request.IntrospectRequest;
import com.tay.dto.response.ApiResponse;
import com.tay.dto.response.AuthenticationResponse;
import com.tay.dto.response.IntrospectResponse;
import com.tay.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ApiResponse.<AuthenticationResponse>builder()
				.result(authenticationService.authenticate(request))
				.build();
	}
	
	@PostMapping("/introspect")
	public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
		return ApiResponse.<IntrospectResponse>builder()
				.result(authenticationService.introspect(request))
				.build();
	}

}
