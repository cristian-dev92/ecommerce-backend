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

    @Autowired
    private com.tienda.ecommerce.service.PdfService pdfService;

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
    /**
     * Endpoint para descargar la factura en PDF de un pedido.
     * Genera el PDF al vuelo combinando Thymeleaf e iText.
     */
    @GetMapping("/{id}/invoice")
    public ResponseEntity<?> downloadInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            // 1. Buscamos el pedido mapeado a tu Record
            OrderResponseDto order = orderService.getOrderById(id);

            // 2. 🛡️ Validación de seguridad:
            // Buscamos si el pedido que intentan descargar pertenece a la ID del usuario del JWT
            // Nota: Como tu Record no expone directamente el ID del usuario, usamos el service o la entidad si fuera necesario.
            // Si el método getOrdersByUser de tu service ya filtra, podemos validar contra el contexto del usuario:
            List<OrderResponseDto> userOrders = orderService.getOrdersByUser(user);
            boolean belongsToUser = userOrders.stream().anyMatch(o -> o.id().equals(id));

            if (!belongsToUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "No tienes permiso para descargar esta factura."));
            }

            // 3. Empaquetamos los datos para el motor de plantillas
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("order", order);
            data.put("user", user);

            // 4. Compilamos el HTML en un buffer de bytes de PDF
            byte[] pdfBytes = pdfService.generatePdfFromHtml("invoice-template", data);

            // 5. Encapsulamos la respuesta binaria para forzar la descarga en Angular
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura-" + order.number() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

}