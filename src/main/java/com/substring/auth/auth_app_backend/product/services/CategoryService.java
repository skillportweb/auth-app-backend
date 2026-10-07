package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.CategoryDto;
import com.substring.auth.auth_app_backend.product.payload.CategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDto addCategory(CategoryRequest request);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(UUID id);

    CategoryDto updateCategory(UUID id, CategoryRequest request);

    void deleteCategory(UUID id);
}