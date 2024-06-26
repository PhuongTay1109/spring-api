package com.tay.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tay.dto.request.UserCreationRequest;
import com.tay.dto.request.UserUpdateRequest;
import com.tay.dto.response.ApiResponse;
import com.tay.dto.response.UserResponse;
import com.tay.model.User;
import com.tay.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
		ApiResponse<User> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.createUser(request));
		return apiResponse;
	}

	@GetMapping
	List<User> getUsers() {
		List<User> users = userService.getUsers();
		for(User user : users) {
			System.out.println(user.getFirstName());
		}
		return userService.getUsers();
	}

	@GetMapping("/{userId}")
	UserResponse getUser(@PathVariable String userId) {
		return userService.getUser(userId);
	}

	@PutMapping("/{userId}")
	User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
		return userService.updateUser(userId, request);
	}

	@DeleteMapping("/{userId}")
	String deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return "User has been deleted";
	}


}
