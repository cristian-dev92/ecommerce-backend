package com.tienda.ecommerce.service;

import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProductService {


    @Autowired
    private ProductRepository productRepository;

    public String uploadProductImage(MultipartFile file) throws IOException {

        // Nombre único
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Guardar archivo en carpeta product-images/
        Path path = Paths.get("product-images/" + fileName);
        Files.write(path, file.getBytes());

        // URL pública
        return "http://localhost:8080/product-images/" + fileName;
    }

    public Product updateProductImage(Long productId, String image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setImageUrl(product.getImageUrl());
        return productRepository.save(product);
    }
}