package com.tay.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tay.dto.request.AuthenticationRequest;
import com.tay.dto.request.IntrospectRequest;
import com.tay.dto.response.AuthenticationResponse;
import com.tay.dto.response.IntrospectResponse;
import com.tay.exception.AppException;
import com.tay.exception.ErrorCode;
import com.tay.model.User;
import com.tay.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final UserRepository userRepository;
	
	@NonFinal 
	@Value("${jwt.signerKey}") // annotation dùng để đọc 1 biến từ file .yml
	private String SIGNER_KEY;
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		// vì mỗi lần hash lại ra output khác nhau dù cùng 1 input nên ko so sánh thẳng đc
		// bcrypt mới có hàm matches đây
		boolean isAuthenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());
		
		if(!isAuthenticated) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		String token = generateToken(request.getUsername());
		
		return AuthenticationResponse.builder()
				.token(token)
				.isAuthenticated(true)
				.build();
	}
	
	private String generateToken(String username) {
		
		// header - chứa typ và alg
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		
		// payload
		// claim - data trong body của payload
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(username) // subject đại diện cho user đăng nhập
				.issuer("tay.com") // token này được issue từ ai - thường là domain của service
				.issueTime(new Date())
				.expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
				.claim("customClaim", "Custom")
				.build();	
		// sau khi có claim set rồi thì tạo ra payload
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		
		// từ header và payload tạo ra JWT object
		JWSObject jwsObject = new JWSObject(header, payload);
		
		// kí token
		// kí đối xứng - khóa để ký và để giải mã trùng nhau
	    try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
		} catch (KeyLengthException e) {
			e.printStackTrace();
		} catch (JOSEException e) {
			e.printStackTrace();
		}

	    // serialize xuống kiểu string
		return jwsObject.serialize();
	}
	
	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		String token = request.getToken();
		
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		
		SignedJWT signedJWT = SignedJWT.parse(token);
		
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		
		 // Verify the token signature and check the expiration date
	    boolean isValid = signedJWT.verify(verifier) && expiryTime.after(new Date());
	    
	    // Build and return the IntrospectResponse
	    return IntrospectResponse.builder()
	            .valid(isValid)
	            .build();
	}
}
