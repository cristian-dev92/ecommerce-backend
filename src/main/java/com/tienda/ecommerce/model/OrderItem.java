package com.tienda.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private double price;

    private int quantity;

    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore // 👈 ESTO ROMPE LA RECURSIÓN
    private Order order;

}
