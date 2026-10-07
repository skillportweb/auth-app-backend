package com.substring.auth.auth_app_backend.product.repositories;

import com.substring.auth.auth_app_backend.product.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    boolean existsByNameIgnoreCaseAndCategoryId(String name, UUID categoryId);

    List<SubCategory> findByCategoryId(UUID categoryId);
}