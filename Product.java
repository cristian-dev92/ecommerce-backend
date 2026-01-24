package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private String name;
    private String description;
    private BigDecimal price; // Usar BigDecimal para dinero, no float/double

    // ... más campos como stock, url_imagen, etc.
}