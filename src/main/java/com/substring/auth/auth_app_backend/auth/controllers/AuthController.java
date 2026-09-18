package com.substring.auth.auth_app_backend.auth.controllers;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.substring.auth.auth_app_backend.auth.payload.LoginRequest;
import com.substring.auth.auth_app_backend.auth.payload.RefreshTokenRequest;
import com.substring.auth.auth_app_backend.auth.payload.TokenResponse;
import com.substring.auth.auth_app_backend.auth.payload.UserDto;
import com.substring.auth.auth_app_backend.auth.entities.RefreshToken;
import com.substring.auth.auth_app_backend.auth.entities.User;
import com.substring.auth.auth_app_backend.auth.repositories.RefreshTokenRepository;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;
import com.substring.auth.auth_app_backend.auth.services.impl.CookiesService;
import com.substring.auth.auth_app_backend.auth.services.impl.JwtService;
import com.substring.auth.auth_app_backend.auth.services.AuthService;

import io.jsonwebtoken.JwtException;

import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final ModelMapper mapper;
	private final CookiesService cookiesService;

	public AuthController(AuthService authService, AuthenticationManager authenticationManager,
			UserRepository userRepository, JwtService jwtService, ModelMapper mapper,
			RefreshTokenRepository refreshTokenRepository, CookiesService cookiesService) {
		this.authService = authService;
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.mapper = mapper;
		this.refreshTokenRepository = refreshTokenRepository;
		this.cookiesService = cookiesService;
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

		// authentication
		Authentication authenticate = authenticate(loginRequest);

		User user = userRepository.findByEmail(loginRequest.email())
				.orElseThrow(() -> new BadCredentialsException("Invalid Username or Password"));

		if (!user.isEnable()) {
			throw new DisabledException("User is disabled");
		}

		String jti = UUID.randomUUID().toString();
		var refreshTokenOb = RefreshToken.builder().jti(jti).user(user).createdAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtiSeconds())).revoked(false).build();

		// refresh token save information
		refreshTokenRepository.save(refreshTokenOb);

		// access token : generate token
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

		// use cookie service to attach refresh token in cookie

		cookiesService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtiSeconds());
		cookiesService.addNoStoreHeaders(response);

		TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtiSeconds(),
				mapper.map(user, UserDto.class));

		return ResponseEntity.ok(tokenResponse);
	}

	private Authentication authenticate(LoginRequest loginRequest) {
		try {
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid Username and Password");
		}
	}

	// access and refresh token karne k lie api

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(
	        @RequestBody(required = false) RefreshTokenRequest body,
	        HttpServletResponse response,
	        HttpServletRequest request) {

	    try {
	        Thread.sleep(5000);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new RuntimeException(e);
	    }

	    String refreshToken = readRefreshTokenFromRequest(body, request)
	            .orElseThrow(() ->
	                    new BadCredentialsException("Refresh Token is missing"));

	    if (!jwtService.isRefreshToken(refreshToken)) {
	        throw new BadCredentialsException("Invailid Refresh Token type");
	    }

	    String jti = jwtService.getJti(refreshToken);

	    UUID userId = jwtService.getUserId(refreshToken);

	    RefreshToken storedRefreshToken =
	            refreshTokenRepository.findByJti(jti)
	                    .orElseThrow(() ->
	                            new BadCredentialsException(
	                                    "Refresh token not recognized"
	                            ));

	    if (storedRefreshToken.isRevoked()) {
	        throw new BadCredentialsException(
	                "Refresh token is expire and revoked"
	        );
	    }

	    if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
	        throw new BadCredentialsException(
	                "Refresh token expired"
	        );
	    }

	    if (!storedRefreshToken.getUser().getId().equals(userId)) {
	        throw new BadCredentialsException(
	                "Refresh token dose not belong to this user"
	        );
	    }

	    // Refresh token ko rotate
	    storedRefreshToken.setRevoked(true);

	    String newJti = UUID.randomUUID().toString();

	    storedRefreshToken.setReplacedByToken(newJti);

	    refreshTokenRepository.save(storedRefreshToken);

	    User user = storedRefreshToken.getUser();

	    var newRefreshTokenOb = RefreshToken.builder()
	            .jti(newJti)
	            .user(user)
	            .createdAt(Instant.now())
	            .expiresAt(
	                    Instant.now()
	                            .plusSeconds(jwtService.getRefreshTtiSeconds())
	            )
	            .revoked(false)
	            .build();

	    refreshTokenRepository.save(newRefreshTokenOb);

	    String newAccessToken =
	            jwtService.generateAccessToken(user);

	    String newRefreshToken =
	            jwtService.generateRefreshToken(
	                    user,
	                    newRefreshTokenOb.getJti()
	            );

	    cookiesService.attachRefreshCookie(
	            response,
	            newRefreshToken,
	            (int) jwtService.getRefreshTtiSeconds()
	    );

	    cookiesService.addNoStoreHeaders(response);

	    return ResponseEntity.ok(
	            TokenResponse.of(
	                    newAccessToken,
	                    newRefreshToken,
	                    jwtService.getAccessTtiSeconds(),
	                    mapper.map(user, UserDto.class)
	            )
	    );
	}
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
	        HttpServletRequest request,
	        HttpServletResponse response) {
	    readRefreshTokenFromRequest(null, request).ifPresent(token -> {
	        try {
	            if (jwtService.isRefreshToken(token)) {
	                String jti = jwtService.getJti(token);
	                refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
	                    rt.setRevoked(true);
	                    refreshTokenRepository.save(rt);
	                });
	            }
	        } catch (JwtException ignored) {

	        }

	    });

	    // clear refresh token cookie
	    cookiesService.clearRefreshCookie(response);
	    // prevent browser/proxy from caching authentication response
	    cookiesService.addNoStoreHeaders(response);
	    // clear security context
	    SecurityContextHolder.clearContext();
	    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	// this method will read refresh token from request header or body
	private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {

		// 1. prefer reading refresh token from cookie
		if (request.getCookies() != null) {
			Optional<String> formCookie = Arrays.stream(request.getCookies())
					.filter(c -> cookiesService.getRefreshTokenCookieName().equals(c.getName())).map(Cookie::getValue)
					.filter(v -> !v.isBlank()).findFirst();
			if (formCookie.isPresent()) {
				return formCookie;
			}
		}
		// 2. body
		if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
			return Optional.of(body.refreshToken());
		}

		// 3. header
		String refreshHeader = request.getHeader("X-Refresh-token");
		if (refreshHeader != null && !refreshHeader.isBlank()) {
			return Optional.of(refreshHeader.trim());
		}

		//4. Authorization = Bearer <Token>
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
			String candidate = authHeader.substring(7).trim();
			if (!candidate.isEmpty()) {
				try {
					if (jwtService.isRefreshToken(candidate)) {
						return Optional.of(candidate);
					}
				} catch (Exception ignored) {
				}
			}
		}

		return Optional.empty();
	}
	
	

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
	}
}
