package com.tienda.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import com.tienda.ecommerce.service.ImageService;
import com.tienda.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products") // La URL base para todos los endpoints de productos
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite Angular
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ImageService imageService;

    // GET - Listar todos
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // GET - Obtener por ID
    @GetMapping("/{id}")
    public Optional<Product> getProductById(
            @PathVariable Long id) {
        return productRepository.findById(id);
    }

    //POST - Cambiamos la ruta para que coincida con Angular y aceptamos Multipart
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Como ya viene la imageUrl en el JSON, solo guardamos
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    // PUT - Actualizar producto
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestBody Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    // DELETE - Eliminar producto
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }
}