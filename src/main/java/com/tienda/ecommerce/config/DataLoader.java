package com.tienda.ecommerce.config;

import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    // Inyección de dependencia del Repositorio
    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ejecuta este código solo si no hay productos ya cargados
        if (productRepository.count() == 0) {

            System.out.println("Cargando productos de prueba...");

            Product p1 = new Product();
            p1.setName("Laptop Gaming X2000");
            p1.setDescription("Potente laptop con tarjeta gráfica RTX 4070 y 32GB de RAM.");
            p1.setPrice(new BigDecimal("1599.99"));

            Product p2 = new Product();
            p2.setName("Monitor Curvo Ultra HD");
            p2.setDescription("Monitor de 32 pulgadas, 144Hz, ideal para trabajo y juegos.");
            p2.setPrice(new BigDecimal("450.50"));

            Product p3 = new Product();
            p3.setName("Teclado Mecánico RGB");
            p3.setDescription("Teclado con switches marrones y luces personalizables.");
            p3.setPrice(new BigDecimal("85.00"));

            // Guardar los productos en la base de datos
            productRepository.save(p1);
            productRepository.save(p2);
            productRepository.save(p3);

            System.out.println("Productos de prueba cargados exitosamente.");
        }
    }
}
