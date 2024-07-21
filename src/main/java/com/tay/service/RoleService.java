package com.tay.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tay.dto.request.RoleRequest;
import com.tay.dto.response.RoleResponse;
import com.tay.mapper.RoleMapper;
import com.tay.model.Role;
import com.tay.repository.PermissionRepository;
import com.tay.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
    	// tạo role - này có thể áp dụng cho cả role đã tồn tại
        Role role = roleMapper.toRole(request);

        // thiết lập quyền
        // JPA sẽ tự động duyệt qua danh sách các ID được cung cấp và tìm tất cả các Permission tương ứng. 
        // Phương thức findAllById của JPA Repository nhận vào một Iterable 
        // (chẳng hạn như List, Set, hoặc bất kỳ loại Collection nào) 
        // và trả về một danh sách các thực thể tương ứng với các ID đó.
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        // lưu role lại
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
        		.stream()
        		.map(roleMapper::toRoleResponse)
        		.toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}