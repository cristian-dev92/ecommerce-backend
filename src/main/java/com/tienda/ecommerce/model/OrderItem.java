package com.tienda.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price; // Precio unitario congelado en el momento del pago

    // Relación con el producto comprado
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Relación hacia atrás con el pedido padre (Evitando bucles con @JsonIgnore)
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /**
     * Lógica de Dominio.
     * Calcula dinámicamente el subtotal de esta línea (Precio congelado * cantidad).
     */
    public BigDecimal getSubtotal() {
        if (this.price == null) return BigDecimal.ZERO;
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}