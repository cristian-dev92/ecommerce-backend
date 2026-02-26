package com.tienda.ecommerce.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateOrderRequest {

    private List<OrderItemRequest> items;

}


