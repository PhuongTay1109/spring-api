package com.tay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tay.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
