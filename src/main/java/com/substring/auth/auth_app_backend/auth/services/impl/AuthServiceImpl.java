package com.substring.auth.auth_app_backend.auth.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.substring.auth.auth_app_backend.auth.payload.UserDto;
import com.substring.auth.auth_app_backend.auth.services.AuthService;
import com.substring.auth.auth_app_backend.auth.services.UserService;

@Service
public class AuthServiceImpl implements AuthService {
	
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	
	
	// Constructor injection
		public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
			this.userService = userService;
			this.passwordEncoder = passwordEncoder;
			
		}
		
	@Override
	public UserDto registerUser(UserDto userDto) {
		
		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
		return userService.createUser(userDto);
	}
}
