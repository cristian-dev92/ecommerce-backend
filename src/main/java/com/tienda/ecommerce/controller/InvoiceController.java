package com.tienda.ecommerce.controller;

import com.tienda.ecommerce.model.Invoice;
import com.tienda.ecommerce.model.User;
import com.tienda.ecommerce.model.UserRole;
import com.tienda.ecommerce.repository.InvoiceRepository;
import com.tienda.ecommerce.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InvoiceController {

    @Autowired private PdfService pdfService;
    @Autowired private InvoiceRepository invoiceRepository;

    /**
     * Endpoint REST seguro para descargar facturas desde Angular.
     * Devuelve el PDF en un flujo de bytes (Blob).
     */
    @GetMapping("/download/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal User user) {

        // 1. Buscamos la factura asociada al pedido
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada para el pedido: " + orderId));

        // 2. CONTROL DE SEGURIDAD CLAVE: Si no es ADMIN y el email no coincide, bloqueamos el acceso
        boolean isAdmin = user.getRoles().contains(UserRole.ROLE_ADMIN);
        if (!isAdmin && !invoice.getUserEmail().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. Preparamos los datos para inyectar en las tablas del HTML
        Map<String, Object> data = new HashMap<>();
        data.put("invoice", invoice);

        // 4. Compilamos el PDF en memoria
        byte[] pdfBytes = pdfService.generatePdfFromHtml("invoice_template", data);

        // 5. Encabezados HTTP limpios para que Angular lo procese como archivo descargable
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("factura_" + invoice.getNumber() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}