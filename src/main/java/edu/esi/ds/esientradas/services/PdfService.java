package edu.esi.ds.esientradas.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Espectaculo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] crearPdfEntrada(Entrada entrada) {
        try (ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream()) {
            Espectaculo espectaculo = entrada.getEspectaculo();
            
            PdfWriter writer = new PdfWriter(pdfBytes);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("ENTRADA ESIentradas").setBold().setFontSize(18));
            document.add(new Paragraph("Artista: " + espectaculo.getArtista()));
            document.add(new Paragraph("Fecha: " + espectaculo.getFecha().toString()));
            document.add(new Paragraph("ID Entrada: " + entrada.getId()));
            document.add(new Paragraph("Precio: " + (entrada.getPrecio() / 100.0) + " euros"));
            
            document.close();
            return pdfBytes.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF técnico.");
        }
    }
}