package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number; // Formato tipo: FACT-2026-0001

    // Snapshot de los datos del cliente para cumplir con la ley de facturación española
    private String nif;
    private String name;
    private String surname;
    private String email;
    private String address;
    private String city;
    private String postalCode;
    private String province;
    private String country;

    // Precisión monetaria absoluta con BigDecimal
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxes; // Cuota del IVA

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    private LocalDate creationDate;
    private LocalDate dueDate;

    // Almacena el email del usuario como String por si se da de baja la cuenta
    private String userEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // Vinculación directa con el pedido original
}