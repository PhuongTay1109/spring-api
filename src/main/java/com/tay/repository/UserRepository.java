package com.tay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tay.model.User;
import java.util.List;
import java.util.Optional;


// controller - quản lý mapping, endpoint
// service - business logic
// repostiory - thao tác với dbms
@Repository
public interface UserRepository extends JpaRepository<User, String> {

	// String tự động generate câu query để kiểm tra sự tồn tại của username
	boolean existsByUsername(String username);
	
	Optional<User> findByUsername(String username);

}
