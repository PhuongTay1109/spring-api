package com.tay.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tay.dto.request.RoleRequest;
import com.tay.dto.response.RoleResponse;
import com.tay.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}	