package com.tay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private final String[] PUBLIC_ENDPOINTS = {
			"/users", "/auth/**"		
	};
	
	@Value("${jwt.signerKey}")
	private String signerKey;
	
	@Autowired
	private CustomJwtDecoder customJwtDecoder;
	
	// khi token được giải mã rồi, các thông tin ở trong token đó sẽ tự động được lưu
	// vào trong authentication trong Security Context
	// bên SecurityConfig khi thêm .hasAuthority sẽ tự động so sánh với authorities trong authentication
	// để cho phép truy cập endpoint hay không
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(request ->
					request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
							//.requestMatchers(HttpMethod.GET, "/users").hasAnyAuthority("ROLE_ADMIN") => phân quyền trên endpoint, còn có phân quyền trên method nữa
					.anyRequest().authenticated());
		
		// khi config oauth2 resource server
		// => đăng ký 1 cái AuthenticationProvider support cho jwt token
		// tức là khi thực hiện 1 request mà cung cấp token vào header Authentication
		// thì sẽ được đưa cho provider này thực hiện authentication
		http.oauth2ResourceServer(oauth2 ->
			oauth2.jwt(jwtConfigurer -> 
						jwtConfigurer.decoder(customJwtDecoder)
									.jwtAuthenticationConverter(jwtAuthenticationConverter()))
		);
		
		return http.build();
	}
	
	// mặc định JwtAuthenticationManager sẽ map các scope trong jwt 
	// và đặt prefix là SCOPE_
	// customize prefix từ SCOPE_ về lại ROLE_
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		
		return jwtAuthenticationConverter;
	}
}
