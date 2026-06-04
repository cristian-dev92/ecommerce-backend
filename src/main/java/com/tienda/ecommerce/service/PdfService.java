package com.tienda.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine; // El motor de Thymeleaf en memoria

    /**
     * Procesa un HTML con datos dinámicos y lo compila en un array de bytes (PDF).
     */
    public byte[] generatePdfFromHtml(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        // 1. Fusionamos los datos con la plantilla HTML de 'resources/templates'
        String htmlContent = templateEngine.process(templateName, context);

        // 2. Pasamos el HTML por el renderizador de iText para generar los bytes del PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error fatal al compilar el PDF de la factura", e);
        }
    }
}
