package com.tienda.ecommerce.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number; // Código público del pedido (Ej: PED-20260604-89)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String status; // PENDING, PAID, SHIPPED, CANCELLED

    @Column(nullable = false)
    private BigDecimal total; // Importe total definitivo facturado

    // Relación con el usuario dueño del pedido
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Vinculamos la entidad User directamente

    // Relación 1:N con las líneas del pedido.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // Método helper para añadir ítems de forma bidireccional segura
    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", number='" + number + "', total=" + total + "}";
    }
}

