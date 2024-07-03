package com.tay.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer.JwtConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final String[] PUBLIC_ENDPOINTS = {
			"/users", "/auth/**"			
	};
	
	@Value("${jwt.signerKey}")
	private String signerKey;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(request ->
					request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
					.anyRequest().authenticated());
		
		// khi config oauth2 resource server
		// => đăng ký 1 cái AuthenticationProvider support cho jwt token
		// tức là khi thực hiện 1 request mà cung cấp token vào header Authentication
		// thì sẽ được đưa cho provider này thực hiện authentication
		http.oauth2ResourceServer(oauth2 ->
			oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
		);
		
		return http.build();
	}
	
	
	// cung cấp cho Spring 1 cái decoder
	// AuthenticationProvider sẽ dùng decoder này để decode cái token
	@Bean
	JwtDecoder jwtDecoder() {
		SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
		return NimbusJwtDecoder
				.withSecretKey(secretKeySpec)
				.macAlgorithm(MacAlgorithm.HS512)
				.build();
		
	}

}
