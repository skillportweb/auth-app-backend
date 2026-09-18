package com.substring.auth.auth_app_backend.auth.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.substring.auth.auth_app_backend.auth.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
   Optional<User> findByEmail(String email);
   boolean existsByEmail(String email);
}
