//package com.tay.config;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.tay.contants.PredefinedRole;
//import com.tay.model.Role;
//import com.tay.model.User;
//import com.tay.repository.RoleRepository;
//import com.tay.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class AppInitConfig {
//	
//	private final PasswordEncoder passwordEncoder;
//	
//	@Bean
//	ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
//		return ArgsAnnotationPointcut -> {
//			if(userRepository.findByUsername("admin").isEmpty()) {
//				Set<Role> roles = new HashSet<>();
//				roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
//				User user = User.builder()
//						.username("admin")
//						.password(passwordEncoder.encode("admin"))
//						.roles(roles)
//						.build();
//				userRepository.save(user);
//				log.warn("admin user has been created with default password: admin");
//			}
//		};
//
//	}
//
//}
