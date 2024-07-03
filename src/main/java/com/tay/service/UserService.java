package com.tay.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tay.dto.request.UserCreationRequest;
import com.tay.dto.request.UserUpdateRequest;
import com.tay.dto.response.UserResponse;
import com.tay.exception.AppException;
import com.tay.exception.ErrorCode;
import com.tay.mapper.UserMapper;
import com.tay.model.User;
import com.tay.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // tạo constructor cho các biến final
public class UserService {
	
	// autorwired không phải best pratice để inject
	// best practice là dùng constructor
	private final UserRepository userRepository;
    private final UserMapper userMapper;

	public User createUser(UserCreationRequest request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}

		User user = userMapper.toUser(request);
		
		// BCrypt là 1 implementation của PasswordEncoder
		// PasswordEncoder là 1 interface của Spring Security cung cấp để các thuật toán implement
		// số càng lớn thì càng mạnh nhưng đánh đổi lại thời gian
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);

	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public UserResponse getUser(String userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		return userMapper.toUserResponse(user);
	}

	public User updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
		userMapper.updateUser(user, request);
		return userRepository.save(user);
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}


}
