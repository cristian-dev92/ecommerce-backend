package com.tienda.ecommerce.service;

import com.tienda.ecommerce.dto.OrderRequest;
import com.tienda.ecommerce.dto.OrderResponseDto;
import com.tienda.ecommerce.model.Invoice;
import com.tienda.ecommerce.model.Order;
import com.tienda.ecommerce.model.OrderItem;
import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.repository.InvoiceRepository;
import com.tienda.ecommerce.repository.OrderItemRepository;
import com.tienda.ecommerce.repository.OrderRepository;
import com.tienda.ecommerce.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InvoiceRepository invoiceRepository;

    /**
     * Procesa la compra desde Angular, descuenta stock en Neon y emite la factura legal de golpe.
     */
    @Transactional
    public OrderResponseDto createOrder(OrderRequest request, User user) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new RuntimeException("El carrito de compras está vacío");
        }

        // 1. Instanciamos la cabecera del Pedido
        Order order = new Order();
        order.setNumber("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("COMPRADO");

        // Listas temporales para procesar en memoria antes de guardar en Neon
        List<OrderItem> itemsToSave = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        // 2. Recorremos los ítems que nos manda Angular mediante el DTO request
        for (OrderRequest.ItemRequest item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.productId()));

            // Validación de seguridad de stock
            if (product.getStock() < item.quantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName()
                        + " (Disponibles: " + product.getStock() + ")");
            }

            // Descontamos el stock de la tienda
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);

            // El precio final ya tiene los descuentos aplicados de Neon
            BigDecimal precioVenta = product.getFinalPrice();
            BigDecimal subtotalItem = precioVenta.multiply(BigDecimal.valueOf(item.quantity()));
            totalPedido = totalPedido.add(subtotalItem);

            // Creamos la línea de detalle
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setPrice(precioVenta); // Foto fija del precio en el detalle

            itemsToSave.add(orderItem);
        }

        // Fijamos los precios totales calculados matemáticamente
        order.setTotal(totalPedido);

        // Guardamos la cabecera del pedido para generar su ID único
        Order savedOrder = orderRepository.save(order);

        // Guardamos todas las líneas de detalle asociadas
        orderItemRepository.saveAll(itemsToSave);

        // 3. 📝 GENERACIÓN AUTOMÁTICA DE LA FACTURA LEGAL (SNAPSHOT)
        generateInvoiceSnapshot(savedOrder, user);

        return mapToResponseDto(savedOrder, itemsToSave);
    }

    /**
     * Recupera de forma cronológica los pedidos realizados por el usuario conectado
     */
    public List<OrderResponseDto> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(order -> mapToResponseDto(order, orderItemRepository.findByOrder(order)))
                .collect(Collectors.toList());
    }

    /**
     * Lógica interna para congelar los datos de facturación (Snapshot anticambios)
     */
    private void generateInvoiceSnapshot(Order order, User user) {
        // Cálculo del IVA del 21% sobre el total de la compra en España
        BigDecimal factorIVA = new BigDecimal("1.21");
        BigDecimal subtotal = order.getTotal().divide(factorIVA, 2, RoundingMode.HALF_UP);
        BigDecimal taxes = order.getTotal().subtract(subtotal);

        Invoice invoice = Invoice.builder()
                .number("FACT-2026-" + String.format("%05d", order.getId())) // Formato secuencial basado en ID
                .order(order)
                .userEmail(user.getEmail())
                .creationDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15)) // 15 días de vencimiento estándar
                // Copia inmutable de los datos del cliente actuales
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .nif(user.getNif() != null ? user.getNif() : "X-0000000-X") // Evita nulls si aún no ha rellenado el perfil
                .address(user.getAddress() != null ? user.getAddress(): "Dirección no especificada")
                .city("")
                .postalCode("")
                .province("")
                .country("España")
                // Precios de contabilidad limpios
                .subtotal(subtotal)
                .taxes(taxes)
                .total(order.getTotal())
                .build();

        invoiceRepository.save(invoice);
    }

    /**
     * Mapeador interno para transformar las entidades JPA en JSONs estructurados para Angular
     */
    private OrderResponseDto mapToResponseDto(Order order, List<OrderItem> details) {
        List<OrderResponseDto.ItemResponseDto> itemsDto = details.stream()
                .map(d -> new OrderResponseDto.ItemResponseDto(
                        d.getProduct().getId(),
                        d.getProduct().getName(),
                        d.getQuantity(),
                        d.getPrice(),
                        d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getNumber(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotal(),
                itemsDto
        );
    }

    /**
     * Recupera un pedido específico por su ID único.
     */
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        List<OrderItem> details = orderItemRepository.findByOrder(order);
        return mapToResponseDto(order, details);
    }
}