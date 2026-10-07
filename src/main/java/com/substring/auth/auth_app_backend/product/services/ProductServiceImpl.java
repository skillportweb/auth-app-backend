package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.ProductDto;
import com.substring.auth.auth_app_backend.product.entities.Category;
import com.substring.auth.auth_app_backend.product.entities.Product;
import com.substring.auth.auth_app_backend.product.entities.SubCategory;
import com.substring.auth.auth_app_backend.product.payload.ProductRequest;
import com.substring.auth.auth_app_backend.product.repositories.CategoryRepository;
import com.substring.auth.auth_app_backend.product.repositories.ProductRepository;
import com.substring.auth.auth_app_backend.product.repositories.SubCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SubCategoryRepository subCategoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public ProductDto addProduct(ProductRequest request) {

        validateCategoryAndSubCategory(
                request.getCategoryId(),
                request.getSubCategoryId()
        );

        Product product = mapRequestToProduct(request);

        String sku = generateSku(request.getProductName());
        product.setSku(sku);

        Product savedProduct = productRepository.save(product);

        return convertToDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        return convertToDto(product);
    }

    @Override
    public ProductDto updateProduct(
            UUID id,
            ProductRequest request
    ) {

        validateCategoryAndSubCategory(
                request.getCategoryId(),
                request.getSubCategoryId()
        );

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        String oldSku = product.getSku();

        mapRequestToExistingProduct(product, request);

        product.setSku(oldSku);

        Product updatedProduct = productRepository.save(product);

        return convertToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }

    private String generateSku(String productName) {

        String prefix = productName
                .replaceAll("[^a-zA-Z]", "")
                .toUpperCase();

        if (prefix.length() > 4) {
            prefix = prefix.substring(0, 4);
        }

        if (prefix.isEmpty()) {
            prefix = "PROD";
        }

        long number = productRepository.count() + 1;

        String sku = prefix + "-" + String.format("%03d", number);

        while (productRepository.existsBySku(sku)) {
            number++;
            sku = prefix + "-" + String.format("%03d", number);
        }

        return sku;
    }

    private Product mapRequestToProduct(ProductRequest request) {

        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setBrand(request.getBrand());

        product.setShortDescription(request.getShortDescription());
        product.setFullDescription(request.getFullDescription());

        product.setMrp(request.getMrp());
        product.setSellingPrice(request.getSellingPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockAlert(request.getLowStockAlert());

        product.setNetWeight(request.getNetWeight());
        product.setUnit(request.getUnit());
        product.setPackagingType(request.getPackagingType());
        product.setCountryOfOrigin(request.getCountryOfOrigin());
        product.setIngredients(request.getIngredients());

        product.setImages(
                request.getImages() != null
                        ? request.getImages()
                        : List.of()
        );

        product.setStatus(request.getStatus());
        product.setFeatured(request.getFeatured());

        SubCategory subCategory = subCategoryRepository
                .findById(request.getSubCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "SubCategory not found with id: "
                                        + request.getSubCategoryId()
                        )
                );

        product.setSubCategory(subCategory);

        return product;
    }

    private void mapRequestToExistingProduct(
            Product product,
            ProductRequest request
    ) {

        product.setProductName(request.getProductName());
        product.setBrand(request.getBrand());

        product.setShortDescription(request.getShortDescription());
        product.setFullDescription(request.getFullDescription());

        product.setMrp(request.getMrp());
        product.setSellingPrice(request.getSellingPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockAlert(request.getLowStockAlert());

        product.setNetWeight(request.getNetWeight());
        product.setUnit(request.getUnit());
        product.setPackagingType(request.getPackagingType());
        product.setCountryOfOrigin(request.getCountryOfOrigin());
        product.setIngredients(request.getIngredients());

        product.setImages(
                request.getImages() != null
                        ? request.getImages()
                        : List.of()
        );

        product.setStatus(request.getStatus());
        product.setFeatured(request.getFeatured());

        SubCategory subCategory = subCategoryRepository
                .findById(request.getSubCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "SubCategory not found with id: "
                                        + request.getSubCategoryId()
                        )
                );

        product.setSubCategory(subCategory);
    }

    private void validateCategoryAndSubCategory(
            UUID categoryId,
            UUID subCategoryId
    ) {

        if (categoryId == null) {
            throw new RuntimeException("Category ID is required");
        }

        if (subCategoryId == null) {
            throw new RuntimeException("SubCategory ID is required");
        }

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + categoryId
                        )
                );

        SubCategory subCategory = subCategoryRepository
                .findById(subCategoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "SubCategory not found with id: "
                                        + subCategoryId
                        )
                );

        if (!subCategory.getCategory().getId().equals(category.getId())) {
            throw new RuntimeException(
                    "SubCategory does not belong to the selected Category"
            );
        }
    }

    private ProductDto convertToDto(Product product) {

        ProductDto dto = new ProductDto();

        dto.setId(product.getId());
        dto.setProductName(product.getProductName());

        SubCategory subCategory = product.getSubCategory();

        if (subCategory != null) {

            dto.setSubCategoryId(subCategory.getId());
            dto.setSubCategoryName(subCategory.getName());

            Category category = subCategory.getCategory();

            if (category != null) {
                dto.setCategoryId(category.getId());
                dto.setCategoryName(category.getName());
            }
        }

        dto.setBrand(product.getBrand());
        dto.setSku(product.getSku());

        dto.setShortDescription(product.getShortDescription());
        dto.setFullDescription(product.getFullDescription());

        dto.setMrp(product.getMrp());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setLowStockAlert(product.getLowStockAlert());

        dto.setNetWeight(product.getNetWeight());
        dto.setUnit(product.getUnit());
        dto.setPackagingType(product.getPackagingType());
        dto.setCountryOfOrigin(product.getCountryOfOrigin());
        dto.setIngredients(product.getIngredients());

        dto.setImages(product.getImages());

        dto.setStatus(product.getStatus());
        dto.setFeatured(product.getFeatured());

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }
}