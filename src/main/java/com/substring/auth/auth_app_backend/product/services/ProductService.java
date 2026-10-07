package com.substring.auth.auth_app_backend.product.services;

import com.substring.auth.auth_app_backend.product.dtos.ProductDto;
import com.substring.auth.auth_app_backend.product.payload.ProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDto addProduct(ProductRequest request);

    List<ProductDto> getAllProducts();

    ProductDto getProductById(UUID id);

    ProductDto updateProduct(UUID id, ProductRequest request);

    void deleteProduct(UUID id);
}