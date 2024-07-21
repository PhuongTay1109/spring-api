package com.tay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tay.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

	List<Permission> findAll();
}
