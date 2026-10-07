package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.SubCategoryDto;
import com.substring.auth.auth_app_backend.product.entities.Category;
import com.substring.auth.auth_app_backend.product.entities.SubCategory;
import com.substring.auth.auth_app_backend.product.payload.SubCategoryRequest;
import com.substring.auth.auth_app_backend.product.repositories.CategoryRepository;
import com.substring.auth.auth_app_backend.product.repositories.SubCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryServiceImpl(
            SubCategoryRepository subCategoryRepository,
            CategoryRepository categoryRepository
    ) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public SubCategoryDto addSubCategory(
            SubCategoryRequest request
    ) {

        validateRequest(request);

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );

        String name = request.getName().trim();

        if (subCategoryRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        name,
                        request.getCategoryId()
                )) {

            throw new RuntimeException(
                    "SubCategory already exists in this Category"
            );
        }

        SubCategory subCategory = new SubCategory();

        subCategory.setName(name);
        subCategory.setDescription(request.getDescription());
        subCategory.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );
        subCategory.setCategory(category);

        SubCategory savedSubCategory =
                subCategoryRepository.save(subCategory);

        return convertToDto(savedSubCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getAllSubCategories() {

        return subCategoryRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getSubCategoriesByCategoryId(
            UUID categoryId
    ) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException(
                    "Category not found with id: " + categoryId
            );
        }

        return subCategoryRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryDto getSubCategoryById(UUID id) {

        SubCategory subCategory =
                subCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SubCategory not found with id: "
                                                + id
                                )
                        );

        return convertToDto(subCategory);
    }

    @Override
    public SubCategoryDto updateSubCategory(
            UUID id,
            SubCategoryRequest request
    ) {

        validateRequest(request);

        SubCategory subCategory =
                subCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SubCategory not found with id: "
                                                + id
                                )
                        );

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );

        String name = request.getName().trim();

        if ((!subCategory.getName().equalsIgnoreCase(name)
                || !subCategory.getCategory().getId()
                .equals(request.getCategoryId()))
                && subCategoryRepository
                .existsByNameIgnoreCaseAndCategoryId(
                        name,
                        request.getCategoryId()
                )) {

            throw new RuntimeException(
                    "SubCategory already exists in this Category"
            );
        }

        subCategory.setName(name);
        subCategory.setDescription(request.getDescription());
        subCategory.setCategory(category);

        if (request.getActive() != null) {
            subCategory.setActive(request.getActive());
        }

        SubCategory updatedSubCategory =
                subCategoryRepository.save(subCategory);

        return convertToDto(updatedSubCategory);
    }

    @Override
    public void deleteSubCategory(UUID id) {

        SubCategory subCategory =
                subCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "SubCategory not found with id: "
                                                + id
                                )
                        );

        subCategoryRepository.delete(subCategory);
    }

    private void validateRequest(
            SubCategoryRequest request
    ) {

        if (request == null) {
            throw new RuntimeException(
                    "SubCategory request cannot be null"
            );
        }

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException(
                    "SubCategory name is required"
            );
        }

        if (request.getCategoryId() == null) {
            throw new RuntimeException(
                    "Category ID is required"
            );
        }
    }

    private SubCategoryDto convertToDto(
            SubCategory subCategory
    ) {

        SubCategoryDto dto = new SubCategoryDto();

        dto.setId(subCategory.getId());
        dto.setName(subCategory.getName());
        dto.setDescription(subCategory.getDescription());
        dto.setActive(subCategory.getActive());

        if (subCategory.getCategory() != null) {
            dto.setCategoryId(
                    subCategory.getCategory().getId()
            );

            dto.setCategoryName(
                    subCategory.getCategory().getName()
            );
        }

        dto.setCreatedAt(subCategory.getCreatedAt());
        dto.setUpdatedAt(subCategory.getUpdatedAt());

        return dto;
    }
}