package com.tienda.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        String number,
        LocalDateTime createdAt,
        String status,
        BigDecimal total,
        List<ItemResponseDto> items
) {
    public record ItemResponseDto(
            Long productId,
            String productName,
            int quantity,
            BigDecimal price,
            BigDecimal subtotal
    ) {}
}