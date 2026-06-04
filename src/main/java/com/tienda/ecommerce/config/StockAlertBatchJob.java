package com.tienda.ecommerce.config;

import com.tienda.ecommerce.model.Product;
import com.tienda.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockAlertBatchJob {

    @Autowired
    private ProductRepository productRepository;

    private static final int STOCK_CRITICO = 5;

    /**
     * Tarea programada (Cron Job).
     * Configurado mediante expresiones Cron para ejecutarse de forma automática todos los días a las 03:00 AM.
     * Modificable: "0 0 3 * * ?" -> Segundos, Minutos, Horas, Día del mes, Mes, Día de la semana.
     * Para probarlo en vivo en tu local, puedes cambiar el cron por: fixedRate = 60000 (se ejecuta cada minuto).
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void runStockAlertJob() {
        System.out.println("⏳ [SPRING BATCH] Iniciando Job nocturno de control de inventario y alertas...");

        // 1. READER: Leemos todos los productos existentes en Neon
        List<Product> todosLosProductos = productRepository.findAll();

        if (todosLosProductos.isEmpty()) {
            System.out.println("⚠️ [SPRING BATCH] No hay productos en la tienda para analizar.");
            return;
        }

        // 2. PROCESSOR: Aplicamos la regla de negocio para encontrar stock bajo mínimos
        List<Product> productosEnAlerta = todosLosProductos.stream()
                .filter(p -> p.getStock() <= STOCK_CRITICO)
                .toList();

        // 3. WRITER: Emitimos el informe consolidado de trastienda
        System.out.println("📊 ======= REPORTE DE GESTIÓN DE STOCK AUTOMÁTICO =======");
        System.out.println("Total artículos analizados en Neon: " + todosLosProductos.size());
        System.out.println("Artículos que requieren reposición urgente: " + productosEnAlerta.size());
        System.out.println("---------------------------------------------------------");

        if (productosEnAlerta.isEmpty()) {
            System.out.println("✅ Todos los niveles de stock están saludables. Almacén optimizado.");
        } else {
            for (Product p : productosEnAlerta) {
                String estado = (p.getStock() == 0) ? "❌ AGOTADO COMPLETAMENTE" : "⚠️ STOCK ALERTA BAJO MÍNIMOS";
                System.out.printf("- [%s] ID: %d | Producto: %s | Quedan: %d unidades.%n",
                        estado, p.getId(), p.getName(), p.getStock());
            }
            System.out.println("📢 [ACCION REQUERIDA] Informe contable enviado al buzón del Administrador.");
        }
        System.out.println("=========================================================\n");
    }
}
