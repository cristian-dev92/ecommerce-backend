package com.tienda.ecommerce.service;

import com.tienda.ecommerce.dto.ProductDetailDto;
import com.tienda.ecommerce.dto.ProductHomeDto;
import com.tienda.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // Métodos públicos para la tienda en Angular (Usa DTOs protegidos y ligeros)
    Page<ProductHomeDto> findAllVisible(Pageable pageable);
    Page<ProductHomeDto> findByCategory(String category, Pageable pageable);
    List<ProductHomeDto> searchAndFilter(String query, String category, List<String> brands, BigDecimal maxPrice);
    List<ProductHomeDto> getActiveOffers();
    ProductDetailDto findDtoById(Long id);

    // Métodos de Administración (CRUD Completo)
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    Product update(Long id, Product updatedProduct);
    void deleteById(Long id);

    // Gestión de Imágenes con Cloudinary
    String uploadImage(MultipartFile file) throws IOException;
}