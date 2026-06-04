package com.tienda.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // Usaremos el email como username para el login

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String surname;
    private String nif;
    private String avatarUrl;

    // Datos de Envío y Facturación unificados de tu compa
    private String address;
    private String city;
    private String postalCode;
    private String province;
    private String country;

    // Sistema de Roles Dinámico sin necesidad de crear tablas intermedias complejas
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>(Set.of(UserRole.ROLE_USER)); // Por defecto es Cliente

    // Lista de Deseos (Wishlist) N:M optimizada para Angular
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private Set<Product> wishlist = new HashSet<>();

    // Tiempos de auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // ========================================================
    // MÉTODOS DE SPRING SECURITY (USERDETAILS)
    // ========================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Transforma nuestro Set de Enums en las autoridades que entiende Spring Security
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email; // El cliente se logueará con su correo
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return this.isActive; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', roles=" + roles + "}";
    }
}
