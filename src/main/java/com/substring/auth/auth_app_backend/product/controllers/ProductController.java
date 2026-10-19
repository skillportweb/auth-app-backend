//package com.substring.auth.auth_app_backend.product.controllers;
//
//import com.substring.auth.auth_app_backend.product.dtos.ProductDto;
//import com.substring.auth.auth_app_backend.product.dtos.ApiResponse;
//import com.substring.auth.auth_app_backend.product.payload.ProductRequest;
//import com.substring.auth.auth_app_backend.product.services.ProductService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/products")
//public class ProductController {
//
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse> addProduct(
//            @RequestBody ProductRequest request) {
//
//        ProductDto product = productService.addProduct(request);
//
//        ApiResponse response = new ApiResponse(
//                "Product added successfully",
//                product
//        );
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<ApiResponse> getAllProducts() {
//
//        List<ProductDto> products = productService.getAllProducts();
//
//        ApiResponse response = new ApiResponse(
//                "Products fetched successfully",
//                products
//        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse> getProductById(
//            @PathVariable UUID id) {
//
//        ProductDto product = productService.getProductById(id);
//
//        ApiResponse response = new ApiResponse(
//                "Product fetched successfully",
//                product
//        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse> updateProduct(
//            @PathVariable UUID id,
//            @RequestBody ProductRequest request) {
//
//        ProductDto product = productService.updateProduct(id, request);
//
//        ApiResponse response = new ApiResponse(
//                "Product updated successfully",
//                product
//        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse> deleteProduct(
//            @PathVariable UUID id) {
//
//        productService.deleteProduct(id);
//
//        ApiResponse response = new ApiResponse(
//                "Product deleted successfully",
//                null
//        );
//
//        return ResponseEntity.ok(response);
//    }
//}



package com.substring.auth.auth_app_backend.product.controllers;

import com.substring.auth.auth_app_backend.product.dtos.ApiResponse;
import com.substring.auth.auth_app_backend.product.dtos.ProductDto;
import com.substring.auth.auth_app_backend.product.payload.ProductRequest;
import com.substring.auth.auth_app_backend.product.services.FileStorageService;
import com.substring.auth.auth_app_backend.product.services.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public ProductController(
            ProductService productService,
            FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    // Upload product image
    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        String imagePath = fileStorageService.saveImage(file);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Image uploaded successfully",
                        "imageUrl", imagePath
                )
        );
    }

    // Add product
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

    // Get all products
    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {

        List<ProductDto> products = productService.getAllProducts();

        ApiResponse response = new ApiResponse(
                "Products fetched successfully",
                products
        );

        return ResponseEntity.ok(response);
    }

    // Get product by ID
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

    // Update product
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

    // Delete product
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
