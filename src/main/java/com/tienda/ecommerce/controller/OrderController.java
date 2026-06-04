package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.dto.OrderRequest;
import com.tienda.ecommerce.dto.OrderResponseDto;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Clave para que Angular pueda consumir la API
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Endpoint para procesar el Checkout desde Angular.
     * Recibe únicamente los IDs de los productos y sus cantidades por seguridad.
     * El @AuthenticationPrincipal inyecta automáticamente el usuario logueado desde el JWT.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest request,
            @AuthenticationPrincipal User user) {
        try {
            OrderResponseDto newOrder = orderService.createOrder(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Captura errores de falta de stock o producto no encontrado
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para que el cliente consulte su historial de pedidos en su panel de Angular.
     * Devuelve la lista ordenada cronológicamente (más recientes primero).
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(
            @AuthenticationPrincipal User user) {
        List<OrderResponseDto> userOrders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(userOrders);
    }
}