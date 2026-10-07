package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.SubCategoryDto;
import com.substring.auth.auth_app_backend.product.payload.SubCategoryRequest;

import java.util.List;
import java.util.UUID;

public interface SubCategoryService {

    SubCategoryDto addSubCategory(SubCategoryRequest request);

    List<SubCategoryDto> getAllSubCategories();

    List<SubCategoryDto> getSubCategoriesByCategoryId(UUID categoryId);

    SubCategoryDto getSubCategoryById(UUID id);

    SubCategoryDto updateSubCategory(
            UUID id,
            SubCategoryRequest request
    );

    void deleteSubCategory(UUID id);
}