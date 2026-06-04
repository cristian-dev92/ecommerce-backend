package com.tienda.ecommerce.config;

import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("🚀 Cargando productos de prueba con stock en Neon...");

            // Multiplicador para calcular el 21% de IVA (Precio * 0.21)
            BigDecimal factorIVA = new BigDecimal("0.21");

            // 1. Laptop Gaming (1599.99€ base -> IVA: 336.00€)
            BigDecimal precioP1 = new BigDecimal("1599.99");
            BigDecimal ivaP1 = precioP1.multiply(factorIVA).setScale(2, RoundingMode.HALF_UP);

            Product p1 = Product.builder()
                    .name("Laptop Gaming X2000")
                    .description("Potente laptop con tarjeta gráfica RTX 4070 y 32GB de RAM.")
                    .price(new BigDecimal("1599.99"))
                    .discount(BigDecimal.ZERO)
                    .taxes(ivaP1) // IVA calculado para este producto
                    .stock(15) // Indispensable para que tu OrderService te deje comprarlo
                    .imageUrl("https://picsum.photos/400/300")
                    .build();

            Product p2 = Product.builder()
                    .name("Monitor Curvo Ultra HD")
                    .description("Monitor de 32 pulgadas, 144Hz, ideal para trabajo y juegos.")
                    .price(new BigDecimal("450.50"))
                    .discount(new BigDecimal("50.00")) // 50€ de descuento de prueba
                    .taxes(ivaP1)
                    .stock(8)
                    .imageUrl("https://picsum.photos/400/300")
                    .build();

            Product p3 = Product.builder()
                    .name("Teclado Mecánico RGB")
                    .description("Teclado con switches marrones y luces personalizables.")
                    .price(new BigDecimal("85.00"))
                    .discount(BigDecimal.ZERO)
                    .taxes(ivaP1)
                    .stock(40)
                    .imageUrl("https://picsum.photos/400/300")
                    .build();

            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);

            System.out.println("✅ Productos de prueba listos para comprar.");
        }
    }
}