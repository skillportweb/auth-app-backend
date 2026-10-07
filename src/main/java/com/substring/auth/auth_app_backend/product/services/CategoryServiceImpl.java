package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.CategoryDto;
import com.substring.auth.auth_app_backend.product.entities.Category;
import com.substring.auth.auth_app_backend.product.payload.CategoryRequest;
import com.substring.auth.auth_app_backend.product.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto addCategory(CategoryRequest request) {

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException("Category name is required");
        }

        if (categoryRepository.existsByNameIgnoreCase(
                request.getName().trim()
        )) {
            throw new RuntimeException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        Category category = new Category();

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );

        Category savedCategory =
                categoryRepository.save(category);

        return convertToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        return convertToDto(category);
    }

    @Override
    public CategoryDto updateCategory(
            UUID id,
            CategoryRequest request
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException("Category name is required");
        }

        String newName = request.getName().trim();

        if (!category.getName().equalsIgnoreCase(newName)
                && categoryRepository.existsByNameIgnoreCase(newName)) {

            throw new RuntimeException(
                    "Category already exists with name: " + newName
            );
        }

        category.setName(newName);
        category.setDescription(request.getDescription());

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updatedCategory =
                categoryRepository.save(category);

        return convertToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {

        CategoryDto dto = new CategoryDto();

        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setActive(category.getActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        return dto;
    }
}