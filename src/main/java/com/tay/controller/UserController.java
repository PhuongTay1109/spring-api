package com.tay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

	private final UserService userService;

	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
		return ApiResponse.<UserResponse>builder()
				.code(HttpStatus.CREATED.value())
				.message("User has been created successfully")
                .result(userService.createUser(request))
                .build();
	}
	
	@GetMapping("/my-info")
	ApiResponse<UserResponse> getMyInfo() {
		return ApiResponse.<UserResponse>builder()
				.code(HttpStatus.OK.value())
				.message("Get user info successfully")
				.result(userService.getMyInfo())
				.build();
	}
	
	// @PreAuthorize được sử dụng để kiểm tra quyền hạn trước khi method được thực thi
	// nếu điều kiện xác thực không thỏa mãn, phương thức sẽ không được thực hiện
	@GetMapping
	//@PreAuthorize("hasRole('ADMIN')")  // dùng với role
	@PreAuthorize("hasAuthority('UPDATE_DATA')") // dùng với permission
	ApiResponse<List<UserResponse>> getUsers() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Username: {}", authentication.getName()); // admin
		authentication.getAuthorities().forEach(grantedAuthority
				-> log.info(grantedAuthority.getAuthority())); // ROLE_ADMIN
		
		
		return ApiResponse.<List<UserResponse>>builder()
				.code(HttpStatus.OK.value())
				.message("Get users list successfully")
				.result(userService.getUsers())
				.build();
	}

	// đảm bảo user đang đăng nhập mới được lấy info user đó
	//	@PostAuthorize được sử dụng để kiểm tra quyền hạn sau khi phương thức đã được thực thi. 
	//	Điều này có nghĩa là phương thức sẽ được thực hiện, 
	//	nhưng kết quả trả về sẽ chỉ được trả về nếu điều kiện xác thực thỏa mãn. 
	//	Nếu điều kiện không thỏa mãn, một ngoại lệ sẽ được ném ra sau khi phương thức đã hoàn thành.
	@GetMapping("/{userId}")
	@PostAuthorize("returnObject.result.username == authentication.name")
	public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
	    return ApiResponse.<UserResponse>builder()
	    		.code(HttpStatus.OK.value())
	            .result(userService.getUser(userId))
	            .build();
	}

    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
        		.code(HttpStatus.NO_CONTENT.value())
        		.message("User has been deleted")
        		.build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
        		.code(HttpStatus.OK.value())
        		.message("User has been updated successfully")
                .result(userService.updateUser(userId, request))
                .build();
    }

}
