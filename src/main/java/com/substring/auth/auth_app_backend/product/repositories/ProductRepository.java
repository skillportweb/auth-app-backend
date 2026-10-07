package com.substring.auth.auth_app_backend.product.repositories;

import com.substring.auth.auth_app_backend.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);
}