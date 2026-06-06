package com.tienda.ecommerce.dto;

import java.math.BigDecimal;

public record ProductHomeDto(
        Long id,
        String name,
        String brand,
        BigDecimal price,       // Precio base
        BigDecimal discount,    // Descuento
        BigDecimal finalPrice,   // PVP calculado
        String imageUrl,
        int stock
) {}
