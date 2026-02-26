package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Setter
    @Getter

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private Long userId;

    @Setter
    @Getter
    private LocalDateTime createdAt;

    @Setter
    @Getter
    private String status;

    @Setter
    @Getter
    private double total;

    @Setter
    @Getter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

}

