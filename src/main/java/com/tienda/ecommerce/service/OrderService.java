package com.tienda.ecommerce.service;

import com.tienda.ecommerce.auth.dto.CreateOrderRequest;
import com.tienda.ecommerce.auth.dto.OrderItemRequest;
import com.tienda.ecommerce.model.Order;
import com.tienda.ecommerce.model.OrderItem;
import com.tienda.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest request, Long userId) {

        Order order = new Order();
        order.setUserId(userId); // Aquí deberías obtener el ID del usuario autenticado
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("pending");

        double total = 0;

        for(OrderItemRequest itemReq : request.getItems()) {

            OrderItem item = new OrderItem();
            item.setProductId(itemReq.getProductId());
            item.setProductName(itemReq.getProductName());
            item.setPrice(itemReq.getPrice());
            item.setQuantity(itemReq.getQuantity());
            item.setSubtotal(itemReq.getPrice() * itemReq.getQuantity());
            item.setOrder(order);

            order.getItems().add(item);
            total += item.getSubtotal();
        }

        order.setTotal(total);

        return orderRepository.save(order);
    }

    public java.util.List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
