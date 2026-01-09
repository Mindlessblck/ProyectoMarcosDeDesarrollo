/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.sonorus.util;

import com.example.sonorus.model.Orden;
import com.example.sonorus.repository.OrdenRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class PdfGenerator {

    private static OrdenRepository ordenRepositoryStatic;

    @Autowired
    public PdfGenerator(OrdenRepository ordenRepository) {
        PdfGenerator.ordenRepositoryStatic = ordenRepository;
    }

    public static void generarBoletaPdf(Long ordenId, OutputStream out) {
        Optional<Orden> opt = ordenRepositoryStatic.findById(ordenId);
        if (opt.isEmpty()) {
            // generar PDF simple de error
            try {
                Document doc = new Document();
                PdfWriter.getInstance(doc, out);
                doc.open();
                doc.add(new Paragraph("Orden no encontrada: " + ordenId));
                doc.close();
            } catch (Exception e) { /* manejar */}
            return;
        }

        Orden orden = opt.get();

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            doc.add(new Paragraph("BOLETA DE COMPRA - SONORUS", h1));
            doc.add(new Paragraph("Número: " + orden.getNumeroBoleta(), normal));
            doc.add(new Paragraph("Fecha: " + orden.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normal));
            doc.add(new Paragraph("Cliente ID: " + orden.getUsuarioId(), normal));
            doc.add(new Paragraph("Dirección: " + orden.getDireccion() + " - " + orden.getCiudad(), normal));
            doc.add(Chunk.NEWLINE);

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Precio unit.");
            table.addCell("Subtotal");

            orden.getDetalles().forEach(d -> {
                table.addCell(d.getNombreProducto());
                table.addCell(String.valueOf(d.getCantidad()));
                table.addCell(String.format("S/ %.2f", d.getPrecioUnitario()));
                table.addCell(String.format("S/ %.2f", d.getSubtotal()));
            });

            doc.add(table);
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("TOTAL: S/ " + String.format("%.2f", orden.getTotal()), h1));

            doc.close();
        } catch (Exception e) {
            // manejar error
            e.printStackTrace();
        }
    }
}
