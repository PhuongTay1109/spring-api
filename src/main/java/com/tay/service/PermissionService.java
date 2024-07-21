package com.tay.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tay.dto.request.PermissionRequest;
import com.tay.dto.response.PermissionResponse;
import com.tay.mapper.PermissionMapper;
import com.tay.model.Permission;
import com.tay.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {
	
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;
	
	public PermissionResponse create(PermissionRequest request) {
		Permission permission = permissionMapper.toPermission(request);
		permission = permissionRepository.save(permission);
		return permissionMapper.toPermissionResponse(permission);
	}
	
	public List<PermissionResponse> getAll() {
		List<Permission> permissions = permissionRepository.findAll();
		return permissions.stream()
				.map(permissionMapper::toPermissionResponse)
				.toList();
	}
	
	public void delete(String permission) {
		permissionRepository.deleteById(permission);
	}

}
