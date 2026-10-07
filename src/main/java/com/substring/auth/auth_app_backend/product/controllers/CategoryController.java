package com.substring.auth.auth_app_backend.product.controllers;

import com.substring.auth.auth_app_backend.product.dtos.CategoryDto;
import com.substring.auth.auth_app_backend.product.dtos.ApiResponse;
import com.substring.auth.auth_app_backend.product.payload.CategoryRequest;
import com.substring.auth.auth_app_backend.product.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addCategory(
            @RequestBody CategoryRequest request) {

        CategoryDto category = categoryService.addCategory(request);

        ApiResponse response = new ApiResponse(
                "Category added successfully",
                category
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {

        List<CategoryDto> categories = categoryService.getAllCategories();

        ApiResponse response = new ApiResponse(
                "Categories fetched successfully",
                categories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(
            @PathVariable UUID id) {

        CategoryDto category = categoryService.getCategoryById(id);

        ApiResponse response = new ApiResponse(
                "Category fetched successfully",
                category
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request) {

        CategoryDto category = categoryService.updateCategory(id, request);

        ApiResponse response = new ApiResponse(
                "Category updated successfully",
                category
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable UUID id) {

        categoryService.deleteCategory(id);

        ApiResponse response = new ApiResponse(
                "Category deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}