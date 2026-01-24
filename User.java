package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
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
    private String email;
    private String password;
    private String avatarUrl;
    private String role = "USER"; // Rol por defecto, útil para seguridad

    @Embedded private Address address;

    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}
    public User(String email, String password, String avatarUrl, Address address) {
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.address = address;
        this.createdAt = LocalDateTime.now();
        this.role = "USER";
    }
// Getters y setters
public Long getId() { return id; }
    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getAvatarUrl() { return avatarUrl; }

    public Address getAddress() { return address; }

    public LocalDateTime getCreatedAt() { return createdAt; }
public String getRole() { return role; }
}
