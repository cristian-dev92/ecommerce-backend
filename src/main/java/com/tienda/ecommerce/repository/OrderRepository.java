package com.tienda.ecommerce.repository;

import com.tienda.ecommerce.model.Order;
import com.tienda.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Recupera el historial completo de pedidos de un usuario ordenado
     * cronológicamente, mostrando los más recientes primero.
     * Al usar el objeto 'User' aprovechamos la potencia de las relaciones de Hibernate.
     */
    List<Order> findByUserOrderByCreatedAtDesc(User user);

}