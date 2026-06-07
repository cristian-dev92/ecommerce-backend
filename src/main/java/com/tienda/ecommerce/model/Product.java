package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad principal del Catálogo que representa un artículo en venta.
 * Centraliza la lógica de negocio relacionada con el cálculo de precios, impuestos y descuentos.
 */
@Entity
@Data // Genera Getters, Setters, toString, etc. gracias a Lombok
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "technical_description", columnDefinition = "TEXT")
    private String technicalDescription;
    @Column
    private String category;

    // Usamos BigDecimal para evitar pérdidas de céntimos en los cálculos de caja
    @Column(nullable = false)
    private BigDecimal price; // Precio base (sin IVA)

    @Column(nullable = false)
    private BigDecimal discount; // Porcentaje de descuento (0-100)

    @Column(nullable = false)
    private BigDecimal taxes; // Porcentaje de impuestos (Ej: 21.00 para España)

    @Builder.Default
    @Column(nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url")
    private String imageUrl; // URL de la imagen principal (Cloudinary)

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "warranty")
    private String warranty;

    // Colección incrustada para la galería de imágenes adicionales
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_gallery", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> gallery = new ArrayList<>();

    @Column(name = "creation_date")
    private LocalDate creationDate;

    // ==========================================
    // LÓGICA DE NEGOCIO (PRECIOS Y DESCUENTOS)
    // ==========================================

    /**
     * Calcula el precio del producto incluyendo los impuestos aplicables (IVA).
     * @return El precio base + IVA redondeado a 2 decimales.
     */
    public BigDecimal getPriceWithTax() {
        if (this.taxes == null || this.taxes.compareTo(BigDecimal.ZERO) <= 0) {
            return this.price;
        }
        // Multiplicador del impuesto: 1 + (taxes / 100) -> Ej: 1 + 0.21 = 1.21
        BigDecimal taxMultiplier = BigDecimal.ONE.add(this.taxes.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return this.price.multiply(taxMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el precio final de venta al público (PVP).
     * Partiendo del precio con impuestos, le aplica el porcentaje de descuento si lo hubiera.
     * @return El precio final listo para pasarela de pago.
     */
    public BigDecimal getFinalPrice() {
        BigDecimal baseWithTax = getPriceWithTax();
        if (this.discount == null || this.discount.compareTo(BigDecimal.ZERO) <= 0) {
            return baseWithTax;
        }

        // Multiplicador del descuento: 1 - (discount / 100) -> Ej: 1 - 0.10 = 0.90
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(this.discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return baseWithTax.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    // Evitamos bucles infinitos en el toString excluyendo colecciones
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}