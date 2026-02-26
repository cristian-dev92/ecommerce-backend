package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String avatarUrl;
    private String role = "USER"; // Rol por defecto

    @Embedded private Address address;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private boolean isActive = true;
    public User() {}
}
