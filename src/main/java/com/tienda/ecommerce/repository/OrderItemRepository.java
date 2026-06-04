package com.tienda.ecommerce.repository;

import com.tienda.ecommerce.model.Order;
import com.tienda.ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Busca las líneas asociadas a un pedido para cuando el usuario mire su historial
    List<OrderItem> findByOrder(Order order);
}
