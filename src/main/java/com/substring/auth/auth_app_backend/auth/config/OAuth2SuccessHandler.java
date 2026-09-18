package com.substring.auth.auth_app_backend.auth.config;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import com.substring.auth.auth_app_backend.auth.services.impl.CookiesService;
import com.substring.auth.auth_app_backend.auth.services.impl.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.substring.auth.auth_app_backend.auth.entities.Provider;
import com.substring.auth.auth_app_backend.auth.entities.RefreshToken;
import com.substring.auth.auth_app_backend.auth.entities.User;
import com.substring.auth.auth_app_backend.auth.repositories.RefreshTokenRepository;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JwtService jwtService;
	private final CookiesService cookiesService;
	private final RefreshTokenRepository refreshTokenRepository;
	
	
	@Value("${app.auth.frontend.success-redirect}")
	private String frontEndSuccessUrl;

	public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService, CookiesService cookiesService,
			RefreshTokenRepository refreshTokenRepository) {

		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.cookiesService = cookiesService;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		logger.info("Successful authentication");
		logger.info("Authentication: {}", authentication);

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

		// Identify OAuth provider
		String registrationId = "unknown";

		if (authentication instanceof OAuth2AuthenticationToken token) {
			registrationId = token.getAuthorizedClientRegistrationId();
		}

		logger.info("registrationId: {}", registrationId);
		logger.info("user: {}", oAuth2User.getAttributes());

		User user;

		switch (registrationId) {

		case "google" -> {

			String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();
			String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
			String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
			String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();

			logger.info("Google ID: {}", googleId);
			logger.info("Google user email: {}", email);
			logger.info("Google user name: {}", name);
			logger.info("Google user picture: {}", picture);

			// Use the SAME persisted User entity if it already exists.
			user = userRepository.findByEmail(email).orElseGet(() -> {

				User newUser = new User();
				newUser.setEmail(email);
				newUser.setName(name);
				newUser.setImage(picture);
				newUser.setEnable(true);
				newUser.setProvider(Provider.GOOGLE);
				newUser.setProviderId(googleId);

				User savedUser = userRepository.save(newUser);
				logger.info("New Google user saved in database. User ID: {}", savedUser.getId());

				return savedUser;
			});

			logger.info("User is ready for login");
			logger.info("User ID: {}", user.getId());
			logger.info("User email: {}", user.getEmail());
			logger.info("User name: {}", user.getName());
		}

		case "github" -> {

			// GitHub attributes: id, login, name, email, avatar_url
			String githubId = oAuth2User.getAttributes().getOrDefault("id", "").toString();
			String login = oAuth2User.getAttributes().getOrDefault("login", "").toString();

			Object emailAttr = oAuth2User.getAttributes().get("email");

			// GitHub often returns email = null when the user's email is private.
			String email = (emailAttr != null && !emailAttr.toString().isBlank())
					? emailAttr.toString()
					: login + "+" + githubId + "@users.noreply.github.com";

			Object nameAttr = oAuth2User.getAttributes().get("name");
			String name = (nameAttr != null && !nameAttr.toString().isBlank()) ? nameAttr.toString() : login;

			String picture = oAuth2User.getAttributes().getOrDefault("avatar_url", "").toString();

			logger.info("GitHub ID: {}", githubId);
			logger.info("GitHub login: {}", login);
			logger.info("GitHub user email (resolved): {}", email);
			logger.info("GitHub user name: {}", name);
			logger.info("GitHub user picture: {}", picture);

			user = userRepository.findByEmail(email).orElseGet(() -> {

				User newUser = new User();
				newUser.setEmail(email);
				newUser.setName(name);
				newUser.setImage(picture);
				newUser.setEnable(true);
				newUser.setProvider(Provider.GITHUB);
				newUser.setProviderId(githubId);

				User savedUser = userRepository.save(newUser);
				logger.info("New GitHub user saved in database. User ID: {}", savedUser.getId());

				return savedUser;
			});

			logger.info("User is ready for login");
			logger.info("User ID: {}", user.getId());
			logger.info("User email: {}", user.getEmail());
			logger.info("User name: {}", user.getName());
		}

		default -> {
			throw new RuntimeException("Invalid registration id: " + registrationId);
		}
		}

		if (user.getId() == null) {
			logger.error("User ID is null. Cannot create RefreshToken for unsaved User.");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User could not be persisted");
			return;
		}

		// CREATE REFRESH TOKEN
		String jti = UUID.randomUUID().toString();
		Instant now = Instant.now();

		RefreshToken refreshTokenOb = RefreshToken.builder().jti(jti).user(user).revoked(false).createdAt(now)
				.expiresAt(now.plusSeconds(jwtService.getRefreshTtiSeconds())).build();

		refreshTokenRepository.save(refreshTokenOb);

		logger.info("Refresh token saved successfully. JTI: {}, User ID: {}", jti, user.getId());

		// GENERATE ACCESS TOKEN
		String accessToken = jwtService.generateAccessToken(user);

		// GENERATE REFRESH TOKEN JWT
		String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

		// ATTACH REFRESH TOKEN COOKIE
		cookiesService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtiSeconds());

		// RESPONSE
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("{" + "\"message\":\"Login successful\"," + "\"user\":\"" + authentication.getName()
				+ "\"," + "\"accessToken\":\"" + accessToken + "\"" + "}");

		logger.info("{} login completed successfully for user ID: {}", registrationId, user.getId());
		response.sendRedirect(frontEndSuccessUrl);
	}
}