package com.tienda.ecommerce.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tienda.ecommerce.dto.ProductDetailDto;
import com.tienda.ecommerce.dto.ProductHomeDto;
import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import com.tienda.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Page<ProductHomeDto> findAllVisible(Pageable pageable) {
        return productRepository.findByVisibleTrue(pageable)
                .map(p -> new ProductHomeDto(p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getStock()));
    }

    @Override
    public Page<ProductHomeDto> findByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndVisibleTrue(category, pageable)
                .map(p -> new ProductHomeDto(p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getStock()));
    }

    @Override
    public List<ProductHomeDto> getActiveOffers() {
        return productRepository.findByDiscountGreaterThanAndVisibleTrue(BigDecimal.ZERO).stream()
                .map(p -> new ProductHomeDto(p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getStock()))
            .collect(Collectors.toList());
}

    /**
     * Motor de búsqueda y filtrado de productos dinámico.
     * Ideal para la barra lateral de filtros en Angular (Marcas, Categorías, Rango de Precios).
     */
    @Override
    public List<ProductHomeDto> searchAndFilter(String query, String category, List<String> brands, BigDecimal maxPrice) {
        List<Product> products;

        if (query != null && !query.trim().isEmpty()) {
            products = productRepository.searchPublicCatalog(query);
        } else if (category != null && !category.trim().isEmpty()) {
            products = productRepository.findByCategoryAndVisibleTrue(category, Pageable.unpaged()).getContent();
        } else {
            products = productRepository.findByVisibleTrue(Pageable.unpaged()).getContent();
        }

        products = new ArrayList<>(products);

        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getBrand() != null && brands.stream().anyMatch(b -> p.getBrand().equalsIgnoreCase(b)))
                    .collect(Collectors.toList());
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            products = products.stream()
                    .filter(p -> p.getFinalPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        // Convertimos la lista filtrada de entidades a DTOs para Angular
        return products.stream()
                .map(p -> new ProductHomeDto(p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getStock()))
                .collect(Collectors.toList());

}
    @Override
    public ProductDetailDto findDtoById(Long id) {
        Product p = findById(id); // Reutiliza el buscador que lanza la excepción si no existe
        return new ProductDetailDto(p.getId(), p.getName(), p.getBrand(), p.getDescription(), p.getTechnicalDescription(), p.getCategory(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getGallery(), p.getStock(), p.getTaxes());
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    public Product save(Product product) {
        if (product == null) throw new IllegalArgumentException("El producto no puede ser nulo");
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, Product updatedProduct) {
        Product existingProduct = findById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setTechnicalDescription(updatedProduct.getTechnicalDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDiscount(updatedProduct.getDiscount());
        existingProduct.setTaxes(updatedProduct.getTaxes());
        existingProduct.setVisible(updatedProduct.isVisible());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setGallery(updatedProduct.getGallery());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Producto no encontrado.");
        }
        productRepository.deleteById(id);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (cloudinary.config.apiKey == null || "0000000000".equals(cloudinary.config.apiKey)) {
            return "https://via.placeholder.com/400x400.png?text=Mock+Local+Image";
        }
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}