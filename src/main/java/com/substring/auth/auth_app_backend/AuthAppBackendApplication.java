package com.substring.auth.auth_app_backend;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.substring.auth.auth_app_backend.auth.config.AppConstants;
import com.substring.auth.auth_app_backend.auth.entities.Role;
import com.substring.auth.auth_app_backend.auth.repositories.RoleRepository;

@SpringBootApplication
public class AuthAppBackendApplication implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(AuthAppBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Admin
        roleRepository.findByName("ROLE_" + AppConstants.ADMIN_ROLE).ifPresentOrElse(
            role -> {
                System.out.println("Admin Role already exists: " + role.getName());
            },
            () -> {
                Role role = new Role();
                role.setName("ROLE_" + AppConstants.ADMIN_ROLE);
                role.setId(UUID.randomUUID());

                roleRepository.save(role);

                System.out.println("Admin Role created: " + role.getName());
            }
        );

        // Guest
        roleRepository.findByName("ROLE_" + AppConstants.GUEST_ROLE).ifPresentOrElse(
            role -> {
                System.out.println("Guest Role already exists: " + role.getName());
            },
            () -> {
                Role role = new Role();
                role.setName("ROLE_" + AppConstants.GUEST_ROLE);
                role.setId(UUID.randomUUID());

                roleRepository.save(role);

                System.out.println("Guest Role created: " + role.getName());
            }
        );
    }
}