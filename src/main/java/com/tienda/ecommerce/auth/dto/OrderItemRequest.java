package com.tienda.ecommerce.auth.dto;

import lombok.Getter;
import lombok.Setter;



    public class OrderItemRequest {

        @Setter
        @Getter
        private Long productId;

        @Setter
        @Getter
        private String productName;

        @Setter
        @Getter
        private Double price;

        @Setter
        @Getter
        private Integer quantity;

}
