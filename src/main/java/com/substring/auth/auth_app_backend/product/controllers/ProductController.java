package com.substring.auth.auth_app_backend.product.controllers;

import com.substring.auth.auth_app_backend.product.dtos.ProductDto;
import com.substring.auth.auth_app_backend.product.dtos.ApiResponse;
import com.substring.auth.auth_app_backend.product.payload.ProductRequest;
import com.substring.auth.auth_app_backend.product.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addProduct(
            @RequestBody ProductRequest request) {

        ProductDto product = productService.addProduct(request);

        ApiResponse response = new ApiResponse(
                "Product added successfully",
                product
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {

        List<ProductDto> products = productService.getAllProducts();

        ApiResponse response = new ApiResponse(
                "Products fetched successfully",
                products
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(
            @PathVariable UUID id) {

        ProductDto product = productService.getProductById(id);

        ApiResponse response = new ApiResponse(
                "Product fetched successfully",
                product
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequest request) {

        ProductDto product = productService.updateProduct(id, request);

        ApiResponse response = new ApiResponse(
                "Product updated successfully",
                product
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable UUID id) {

        productService.deleteProduct(id);

        ApiResponse response = new ApiResponse(
                "Product deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}