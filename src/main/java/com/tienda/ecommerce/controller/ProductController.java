package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import com.tienda.ecommerce.service.ImageService;
import com.tienda.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "http://localhost:4200") // Permite Angular
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ImageService imageService;

    // GET - Listar todos
    @GetMapping public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // GET - Obtener por ID
    @GetMapping("/{id}") public Optional<Product> getProductById(
            @PathVariable Long id) {
        return productRepository.findById(id);
    }

    // POST - Crear producto
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
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

    //SUBIR IMÁGENES DE PRODUCTOS CON CLOUDINARY
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadProductImage(
            @RequestParam("image") MultipartFile file
    ) throws IOException {

        String url = imageService.uploadImage(file);

        return ResponseEntity.ok(Map.of("imageUrl", url));
    }

    // ASIGNAR IMAGEN A PRODUCTO
    @PutMapping("/{id}/image") public ResponseEntity<?> setProductImageUrl(
            @PathVariable Long id,
            @RequestBody Map<String, String> body ) {
        String imageUrl = body.get("imageUrl");
        Product updated = productService.updateProductImage(id, imageUrl);
        return ResponseEntity.ok(updated);
    }
}