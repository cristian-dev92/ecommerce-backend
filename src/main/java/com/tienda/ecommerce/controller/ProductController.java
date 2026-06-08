package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.dto.ProductDetailDto;
import com.tienda.ecommerce.dto.ProductHomeDto;
import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Crucial para que Angular no dé errores de CORS
public class ProductController {

    @Autowired
    private ProductService productService;

    // ==========================================
    // ENDPOINTS PÚBLICOS (Para Clientes en Angular)
    // ==========================================

    /**
     * Lista todos los productos visibles paginados.
     * URL en Angular: /api/products?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<ProductHomeDto>> getAllVisible(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findAllVisible(pageable));
    }

    /**
     * Filtra productos visibles por categoría de forma paginada.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductHomeDto>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findByCategory(category, pageable));
    }

    /**
     * El Súper-Filtro lateral de Angular.
     * Permite encadenar búsquedas por texto, categoría, marcas múltiples y precio máximo.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductHomeDto>> searchAndFilter(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
    ) {
        Page<ProductHomeDto> result = productService.searchAndFilter(query, category, brands, maxPrice, pageable);
        return ResponseEntity.ok(productService.searchAndFilter(query, category, brands, maxPrice, pageable));
    }

    /**
     * Devuelve los productos destacados en oferta.
     */
    @GetMapping("/offers")
    public ResponseEntity<List<ProductHomeDto>> getOffers() {
        return ResponseEntity.ok(productService.getActiveOffers());
    }

    /**
     * Detalle de un producto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findDtoById(id));
    }

    // ==========================================
    // ENDPOINTS PRIVADOS (Para el Panel Admin de Angular)
    // ==========================================

    /**
     * Crear un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Modificar un producto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    /**
     * Eliminar un producto físicamente de la base de datos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build(); // Devuelve un estado 204 sin cuerpo
    }

    /**
     * Endpoint independiente para subir imágenes a Cloudinary desde el Panel Admin.
     * Angular enviará el archivo aquí, recibirá la URL de Cloudinary y la meterá en el objeto Product.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = productService.uploadImage(file);
            return ResponseEntity.ok().body(java.util.Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Error al subir imagen: " + e.getMessage()));
        }
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getBrands() {
        List<String> brands = productService.getAllDistinctBrands();
        return ResponseEntity.ok(brands);
    }
}