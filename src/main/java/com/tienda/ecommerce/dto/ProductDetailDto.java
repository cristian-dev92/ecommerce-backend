package com.tienda.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long id,
        String name,
        String brand,
        String description,
        String technicalDescription,
        String category,
        BigDecimal price,
        BigDecimal discount,
        BigDecimal finalPrice,
        String imageUrl,
        List<String> gallery,
        int stock,
        BigDecimal taxes
) {}