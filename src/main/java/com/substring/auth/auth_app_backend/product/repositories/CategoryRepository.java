package com.substring.auth.auth_app_backend.product.repositories;

import com.substring.auth.auth_app_backend.product.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameIgnoreCase(String name);
}