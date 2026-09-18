package com.substring.auth.auth_app_backend.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.substring.auth.auth_app_backend.auth.entities.RefreshToken;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

	Optional<RefreshToken> findByJti(String jti);

}
