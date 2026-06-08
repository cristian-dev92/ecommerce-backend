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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageImpl;

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

    @PersistenceContext
    private EntityManager entityManager;

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
    public Page<ProductHomeDto> searchAndFilter(String query, String category, List<String> brands, BigDecimal maxPrice, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // 1. Consulta para obtener los productos
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);
    List<Predicate> predicates = new ArrayList<>();

    // REGLA FIJA: Solo productos visibles en la tienda
    predicates.add(cb.isTrue(root.get("visible")));

    // Filtro 1: Query global (Nombre o descripción)
    if (query != null && !query.trim().isEmpty()) {
        String match = "%" + query.trim().toLowerCase() + "%";
        Predicate nameLike = cb.like(cb.lower(root.get("name")), match);
        Predicate descLike = cb.like(cb.lower(root.get("description")), match);
        predicates.add(cb.or(nameLike, descLike));
    }

    // Filtro 2: Categoría exacta
    if (category != null && !category.trim().isEmpty()) {
        predicates.add(cb.equal(root.get("category"), category));
    }

    // Filtro 3: Marcas EXACTAS (Aquí matamos el error de LG que mezcla marcas)
    if (brands != null && !brands.isEmpty()) {
        Expression<String> brandExpression = root.get("brand");
        predicates.add(brandExpression.in(brands));
    }

    // Filtro 4: Precio Máximo aplicando el precio final calculado
    if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
        predicates.add(cb.lessThanOrEqualTo(root.get("finalPrice"), maxPrice));
    }

    cq.where(predicates.toArray(new Predicate[0]));

    // 2. Ejecutar la query con los límites de la paginación (offset y limit)
    List<Product> resultProducts = entityManager.createQuery(cq)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

    // 3. Consulta secundaria para contar el TOTAL de elementos (Imprescindible para que Angular calcule las páginas totales)
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Product> countRoot = countQuery.from(Product.class);
    List<Predicate> countPredicates = new ArrayList<>();

    // Clonamos los mismos filtros para el contador
    countPredicates.add(cb.isTrue(countRoot.get("visible")));
    if (query != null && !query.trim().isEmpty()) {
        String match = "%" + query.trim().toLowerCase() + "%";
        countPredicates.add(cb.or(
                cb.like(cb.lower(countRoot.get("name")), match),
                cb.like(cb.lower(countRoot.get("description")), match)
        ));
    }
    if (category != null && !category.trim().isEmpty()) {
        countPredicates.add(cb.equal(countRoot.get("category"), category));
    }
    if (brands != null && !brands.isEmpty()) {
        countPredicates.add(countRoot.get("brand").in(brands));
    }
    if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
        countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("finalPrice"), maxPrice));
    }

    countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

    // 4. Mapeamos la página de entidades al DTO ligero de Angular
    List<ProductHomeDto> dtos = resultProducts.stream()
            .map(p -> new ProductHomeDto(p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getDiscount(), p.getFinalPrice(), p.getImageUrl(), p.getStock()))
            .toList();

    return new PageImpl<>(dtos, pageable, totalElements);
}

    @Override
    public ProductDetailDto findDtoById(Long id) {
        Product p = findById(id); // Reutiliza el buscador que lanza la excepción si no existe
        return new ProductDetailDto(
                p.getId(), p.getName(), p.getBrand(), p.getDescription(),
                p.getTechnicalDescription(), p.getCategory(), p.getPrice(), p.getDiscount(), p.getFinalPrice(),
                p.getImageUrl(), p.getGallery(), p.getStock(), p.getTaxes(),
                p.getManufacturer(), p.getWarranty());
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

    @Override
    public List<String> getAllDistinctBrands() {
        return productRepository.findDistinctBrands();
    }

}