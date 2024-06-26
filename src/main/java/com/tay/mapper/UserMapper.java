package com.tay.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tay.dto.request.UserCreationRequest;
import com.tay.dto.request.UserUpdateRequest;
import com.tay.dto.response.UserResponse;
import com.tay.model.User;

// báo cho mapstruct biết sử dụng theo spring
@Mapper(componentModel = "spring")
public interface UserMapper {

	User toUser(UserCreationRequest request);
	
	// define là mapping từ request sang user
	void updateUser(@MappingTarget User user, UserUpdateRequest request);
	
	//@Mapping(target = "lastName", ignore = true)
	UserResponse toUserResponse(User user);
	
	// nếu field của 2 object khác nhau
	// ignore thì ko mapping field đó
	// dùng annotation là @Mapping(source = "", target = "", ignore = true/false)
	// còn nhiều annotation nữa

}
