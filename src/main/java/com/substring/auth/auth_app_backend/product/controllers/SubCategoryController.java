package com.substring.auth.auth_app_backend.product.controllers;

import com.substring.auth.auth_app_backend.product.dtos.ApiResponse;
import com.substring.auth.auth_app_backend.product.dtos.SubCategoryDto;
import com.substring.auth.auth_app_backend.product.payload.SubCategoryRequest;
import com.substring.auth.auth_app_backend.product.services.SubCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sub-categories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addSubCategory(
            @RequestBody SubCategoryRequest request) {

        SubCategoryDto subCategory =
                subCategoryService.addSubCategory(request);

        ApiResponse response = new ApiResponse(
                "Sub-category added successfully",
                subCategory
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllSubCategories() {

        List<SubCategoryDto> subCategories =
                subCategoryService.getAllSubCategories();

        ApiResponse response = new ApiResponse(
                "Sub-categories fetched successfully",
                subCategories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getSubCategoriesByCategoryId(
            @PathVariable UUID categoryId) {

        List<SubCategoryDto> subCategories =
                subCategoryService.getSubCategoriesByCategoryId(categoryId);

        ApiResponse response = new ApiResponse(
                "Sub-categories fetched successfully",
                subCategories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSubCategoryById(
            @PathVariable UUID id) {

        SubCategoryDto subCategory =
                subCategoryService.getSubCategoryById(id);

        ApiResponse response = new ApiResponse(
                "Sub-category fetched successfully",
                subCategory
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSubCategory(
            @PathVariable UUID id,
            @RequestBody SubCategoryRequest request) {

        SubCategoryDto subCategory =
                subCategoryService.updateSubCategory(id, request);

        ApiResponse response = new ApiResponse(
                "Sub-category updated successfully",
                subCategory
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSubCategory(
            @PathVariable UUID id) {

        subCategoryService.deleteSubCategory(id);

        ApiResponse response = new ApiResponse(
                "Sub-category deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}