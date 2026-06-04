package com.tienda.ecommerce.dto;

import java.util.List;

public record OrderRequest(
        List<ItemRequest> items
) {
    public record ItemRequest(
            Long productId,
            int quantity
    ) {}
}
