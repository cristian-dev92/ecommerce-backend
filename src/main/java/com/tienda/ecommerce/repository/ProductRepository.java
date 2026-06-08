package com.tienda.ecommerce.repository;

import com.tienda.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Product.
 * Proporciona métodos CRUD avanzados, filtrados y paginación para el catálogo.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Catálogo público: Solo productos visibles (Soportando paginación para Angular)
    Page<Product> findByVisibleTrue(Pageable pageable);

    // 2. Filtrar por categoría en el catálogo público
    Page<Product> findByCategoryAndVisibleTrue(String category, Pageable pageable);

    // 3. Recuperar productos rebajados (Ofertas) que estén visibles
    List<Product> findByDiscountGreaterThanAndVisibleTrue(BigDecimal discount);

    // 4. Buscador inteligente para el panel de administración (Busca todo, visible o no)
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * 5. Buscador inteligente para clientes (Catálogo Público).
     * Usamos @Query para asegurar que los paréntesis lógicos protejan la regla de "visible = true"
     * y evitar el método larguísimo y peligroso por derivación.
     */
    @Query("SELECT p FROM Product p WHERE p.visible = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            " LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchPublicCatalog(@Param("query") String query);

    // Añade este método para obtener las marcas únicas y limpias de la base de datos
    @Query("SELECT DISTINCT TRIM(p.brand) FROM Product p WHERE p.brand IS NOT NULL AND TRIM(p.brand) != '' ORDER BY TRIM(p.brand) ASC")
    List<String> findDistinctBrands();
}
