package com.tay.mapper;

import org.mapstruct.Mapper;

import com.tay.dto.request.PermissionRequest;
import com.tay.dto.response.PermissionResponse;
import com.tay.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
	
	Permission toPermission(PermissionRequest request);
	PermissionResponse toPermissionResponse(Permission permission);

}
