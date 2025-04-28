package com.proyecto.galeria.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.proyecto.galeria.model.reporte;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportePdfService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] buildPdf(List<reporte> reportes, String titulo) throws Exception {

        var baos   = new ByteArrayOutputStream();
        var writer = new PdfWriter(baos);
        var pdf    = new PdfDocument(writer);
        var doc    = new Document(pdf);

        var font   = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        doc.setFont(font);

        doc.add(new Paragraph(titulo).setBold().setFontSize(14));

        for (reporte r : reportes) {
            doc.add(new Paragraph(
                    FMT.format(r.getFechaHora()) + " – " + r.getAutor().getNombre())
                    .setBold()
                    .setMarginTop(10));
            doc.add(new Paragraph(r.getContenido()));
            doc.add(new Paragraph("\n"));
        }

        doc.close();
        return baos.toByteArray();
    }
}
