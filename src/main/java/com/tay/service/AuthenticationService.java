package com.tay.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import com.tay.dto.request.LogoutRequest;
import com.tay.dto.response.AuthenticationResponse;
import com.tay.dto.response.IntrospectResponse;
import com.tay.exception.AppException;
import com.tay.exception.ErrorCode;
import com.tay.model.InvalidatedToken;
import com.tay.model.User;
import com.tay.repository.InvalidatedTokenRepository;
import com.tay.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final InvalidatedTokenRepository invalidatedTokenRepository;

	@NonFinal
	@Value("${jwt.signerKey}") // annotation dùng để đọc 1 biến từ file .yml
	private String SIGNER_KEY;

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		// vì mỗi lần hash lại ra output khác nhau dù cùng 1 input nên ko so sánh thẳng
		// đc
		// bcrypt mới có hàm matches đây
		boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!isAuthenticated) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}

		String token = generateToken(user);

		return AuthenticationResponse.builder().token(token).isAuthenticated(true).build();
	}

	// Để spring có thể handle được authorization
	// thì thông tin về role cần được có ở trong token
	private String generateToken(User user) {

		// header - chứa typ và alg
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		// payload
		// claim - data trong body của payload
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUsername()) // subject đại diện cho user
																							// đăng nhập
				.issuer("tay.com") // token này được issue từ ai - thường là domain của service
				.issueTime(new Date()).expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
				.claim("scope", buildScope(user)) // để role trong scope theo chuẩn oauth2 để spring security map role
													// trong scope vào authority
				.jwtID(UUID.randomUUID().toString()).build();
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

	// các scope trong oauth2 phân cách nhau bằng dấu cách
	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");
		if (!CollectionUtils.isEmpty(user.getRoles())) {
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!CollectionUtils.isEmpty(role.getPermissions()))
					role.getPermissions().forEach(permission -> {
						stringJoiner.add(permission.getName());
					});
			});
		}
		return stringJoiner.toString();
	}

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		String token = request.getToken();
		try {
			verifyToken(token);
			return new IntrospectResponse(true);
		}
		catch (AppException e) {
			return new IntrospectResponse(false);
		}
	}

	public void logout(LogoutRequest request) throws JOSEException, ParseException {
		SignedJWT signedJWT = verifyToken(request.getToken());
		String jit = signedJWT.getJWTClaimsSet().getJWTID();
		Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();
		
		InvalidatedToken invalidatedToken = new InvalidatedToken(jit, expiryDate);
		invalidatedTokenRepository.save(invalidatedToken);
	}

	private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		boolean verified = signedJWT.verify(verifier);
		if(!(verified && expiryTime.after(new Date())))
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		
		if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		
		return signedJWT;
	}

}
