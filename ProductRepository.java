package com.tienda.ecommerce.repository;

import com.tienda.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Boot implementará automáticamente métodos como save(), findById(), findAll(), etc.
}

