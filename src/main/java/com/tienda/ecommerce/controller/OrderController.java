package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.auth.dto.CreateOrderRequest;
import com.tienda.ecommerce.model.Order;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request,
                             @AuthenticationPrincipal User user) {
        return orderService.createOrder(request, user.getId());
    }

    @GetMapping
    public List<Order> getOrders(@AuthenticationPrincipal com.tienda.ecommerce.model.User user) {
        return orderService.getOrdersByUser(user.getId());
    }
}
